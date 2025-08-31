package com.nutrisport.backend

import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        configureRouting()
    }.start(wait = true)
}

fun Application.configureRouting() {
    val httpClient = HttpClient()

    routing {
        post("/api/auth/google") {
            val request = call.receive<AuthCodeRequest>()

            // Exchange code with Google
            val response: HttpResponse = httpClient.post("https://oauth2.googleapis.com/token") {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "code" to request.authCode,
                        "client_id" to System.getenv("GOOGLE_CLIENT_ID"),
                        "client_secret" to System.getenv("GOOGLE_CLIENT_SECRET"),
                        "redirect_uri" to "",
                        "grant_type" to "authorization_code"
                    )
                )
            }

            if (response.status.isSuccess()) {
                val tokens: GoogleTokenResponse = response.body()
                call.respond(tokens)
            } else {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Failed to exchange code with Google")
                )
            }
        }
    }
}

@Serializable
data class AuthCodeRequest(val authCode: String)

@Serializable
data class GoogleTokenResponse(
    val access_token: String? = null,
    val refresh_token: String? = null,
    val expires_in: Int? = null,
    val scope: String? = null,
    val token_type: String? = null
)
