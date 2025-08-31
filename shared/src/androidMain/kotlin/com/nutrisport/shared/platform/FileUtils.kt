package com.nutrisport.shared.platform

import android.content.Context
import android.net.Uri
import dev.gitlive.firebase.storage.File
import com.nutrisport.core.AppContextHolder
import dev.gitlive.firebase.storage.StorageReference
import androidx.core.net.toUri

actual class FileUtils {

    actual fun fileToByteArrayAndName(photoUri: PhotoUri): FileData {
        val uri = photoUri.path.toUri()
        val context = AppContextHolder.appContext

        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        val name = uri.lastPathSegment ?: "image.jpg"

        return FileData(bytes, name)
    }
}
