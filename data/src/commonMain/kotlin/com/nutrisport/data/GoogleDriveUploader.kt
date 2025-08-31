package com.nutrisport.data

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.* // or Android, depending on your KMP target
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.timeout
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.OutgoingContent
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.close
import io.ktor.utils.io.writeFully
import kotlin.random.Random

/**
 * Response model for Google Drive file upload
 */
@Serializable
data class DriveFileResponse(
    val id: String? = null,
    val name: String? = null,
    val webViewLink: String? = null
)

sealed class DriveApiError(message: String, val code: Int? = null) : Exception(message) {
    class Unauthorized(msg: String = "Unauthorized (401)") : DriveApiError(msg, 401)
    class Forbidden(msg: String = "Forbidden (403)") : DriveApiError(msg, 403)
    class ApiError(val statusCode: Int, msg: String) : DriveApiError(msg, statusCode)
    class NetworkError(msg: String) : DriveApiError(msg)
}

@Serializable
data class DriveFileResult(val id: String, val webContentLink: String?)

@Serializable
data class DriveFileMetadata(
    val name: String,
    val mimeType: String,
    val parents: List<String>? = null
)

/**
 * Responsible only for uploading files to Google Drive.
 * No UI dependencies, ready for KMP shared code.
 */
class GoogleDriveUploader(
    //private val httpClient: HttpClient = defaultHttpClient()
) {

    private val httpClient = defaultHttpClient()

    companion object {
        private fun defaultHttpClient(): HttpClient =
            HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    })
                }
                install(Logging) {
                    level = LogLevel.BODY
                }
            }
    }

    suspend fun getFolderId(token: String?, folderName: String): String? {
        val response: Map<String, List<Map<String, String>>> = httpClient.get(
            "https://www.googleapis.com/drive/v3/files"
        ) {
            header(HttpHeaders.Authorization, "Bearer $token")
            parameter("q", "mimeType='application/vnd.google-apps.folder' and name='$folderName' and trashed=false")
            parameter("fields", "files(id,name)")
        }.body()

        return response["files"]?.firstOrNull()?.get("id")
    }

    suspend fun downloadImage(
        accessToken: String?,
        fileId: String
    ): ByteArray {
        val client = HttpClient(CIO) {
            install(Logging) { level = LogLevel.BODY }
        }

        val response: HttpResponse = client.get("https://www.googleapis.com/drive/v3/files/$fileId") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            url {
                parameters.append("alt", "media")
            }
        }

        if (!response.status.isSuccess()) {
            throw Exception("Failed to download image: ${response.status.value} ${response.bodyAsText()}")
        }

        return response.body()
    }

    /**
     * Upload an image to Drive using multipart upload (metadata JSON + binary).
     *
     * - token: OAuth2 access token with scope drive.file (Bearer)
     * - name: desired filename in Drive
     * - mimeType: e.g. "image/jpeg"
     * - bytes: binary bytes of the file
     * - parentFolderId: optional parent folder ID
     *
     * Throws DriveApiError on failures.
     */
    suspend fun uploadImage(
        accessToken: String?,
        fileName: String,
        mimeType: String,
        bytes: ByteArray,
        parentFolderId: String? = null
    ): DriveFileResponse {
        val json = Json { ignoreUnknownKeys = true }
        val boundary = "-------drive-multipart-${Random.nextLong().toString(16)}"
        val meta = DriveFileMetadata2(name = fileName, parents = parentFolderId?.let { listOf(it) })
        val metadataJson = json.encodeToString(DriveFileMetadata2.serializer(), meta)

        val metadataPartHeader =
            "--$boundary\r\nContent-Type: application/json; charset=UTF-8\r\n\r\n"
        val filePartHeader =
            "\r\n--$boundary\r\nContent-Type: $mimeType\r\nContent-Transfer-Encoding: binary\r\n\r\n"
        val trailer = "\r\n--$boundary--"

        // We will stream the body using WriteChannelContent (multiplatform-safe).
        val body = object : OutgoingContent.WriteChannelContent() {
            override val contentType: ContentType? =
                ContentType.parse("multipart/related; boundary=$boundary")

            override suspend fun writeTo(channel: ByteWriteChannel) {
                // write metadata
                channel.writeFully(metadataPartHeader.encodeToByteArray())
                channel.writeFully(metadataJson.encodeToByteArray())
                // write file header
                channel.writeFully(filePartHeader.encodeToByteArray())
                // write file bytes
                channel.writeFully(bytes)
                // write trailer
                channel.writeFully(trailer.encodeToByteArray())
                channel.close()
            }
        }

        val url = "https://www.googleapis.com/upload/drive/v3/files"
        try {
            val response: HttpResponse = httpClient.post(url) {
                url {
                    parameters.append("uploadType", "multipart")
                    parameters.append("fields", "id,name,webViewLink")
                }
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                // contentType set in body above
                setBody(body)
                timeout {
                    requestTimeoutMillis = 120_000
                }
            }

            val code = response.status.value
            val text = response.bodyAsText()
            return when (code) {
                in 200..299 -> json.decodeFromString(DriveFileResponse.serializer(), text)
                401 -> throw DriveApiError.Unauthorized()
                403 -> throw DriveApiError.Forbidden(text)
                else -> throw DriveApiError.ApiError(code, "Drive API error: $text")
            }
        } catch (e: ClientRequestException) {
            // 4xx
            val status = e.response.status.value
            val bodyText = try { e.response.bodyAsText() } catch (_: Exception) { e.message ?: "" }
            throw DriveApiError.ApiError(status, bodyText)
        } catch (e: ServerResponseException) {
            val status = e.response.status.value
            val bodyText = try { e.response.bodyAsText() } catch (_: Exception) { e.message ?: "" }
            throw DriveApiError.ApiError(status, bodyText)
        } catch (e: Exception) {
            throw DriveApiError.NetworkError(e.message ?: "Network error")
        }
    }

    /**
     * Uploads an image to Google Drive using multipart (metadata + binary).
     *
     * @param accessToken The OAuth 2.0 access token from Android GIS/AppAuth.
     * @param fileName Desired name in Drive
     * @param mimeType File MIME type, e.g. "image/jpeg"
     * @param bytes File contents as ByteArray
     * @param parentFolderId Optional parent folder
     */
    suspend fun uploadImage(
        accessToken: String?,
        fileName: String,
        mimeType: String,
        bytes: ByteArray,
    ): DriveFileResult {
        val parents = getFolderId(accessToken, "PublicImages")
        val metadata = buildJsonMetadata(fileName, mimeType, folderId = parents)

        val response: HttpResponse = httpClient.submitFormWithBinaryData(
            url = "https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart",
            formData = formData {
                append(
                    key = "metadata",
                    value = metadata,
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, "application/json; charset=UTF-8")
                        append(HttpHeaders.ContentDisposition, "form-data; name=\"metadata\"")
                    }
                )
                append(
                    key = "file",
                    value = bytes,
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, mimeType)
                        append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"$fileName\"")
                    }
                )
            }
        ) {
            method = HttpMethod.Post
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessToken")
            }
        }

        if (!response.status.isSuccess()) {
            throw Exception("Upload failed: ${response.status.value} ${response.bodyAsText()}")
        }

        val apiResponse: DriveFileResponse = response.body()
        val fileId = apiResponse.id ?: throw Exception("No file ID returned from Drive")

        return DriveFileResult(
            id = fileId,
            webContentLink = apiResponse.webViewLink
        )
    }

    fun buildJsonMetadata(fileName: String, mimeType: String, folderId: String?): String {
        val metadata = DriveFileMetadata(
            name = fileName,
            mimeType = mimeType,
            parents = folderId?.let { listOf(it) } // Wrap folderId in a list
        )
        return Json.encodeToString(metadata)
    }


    /**
     * Delete file by Drive fileId.
     * Returns true on 2xx, false otherwise.
     */
    suspend fun deleteFile(accessToken: String?, fileId: String): Boolean {
        val client: HttpClient = createHttpClient()
        val response = client.delete("https://www.googleapis.com/drive/v3/files/$fileId") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }
        return response.status.value in 200..299
    }
}
