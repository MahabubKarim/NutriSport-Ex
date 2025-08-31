package com.nutrisport.shared.platform

import androidx.compose.runtime.Composable
import dev.gitlive.firebase.storage.File

data class FileData(val bytes: ByteArray?, val name: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FileData

        if (!bytes.contentEquals(other.bytes)) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bytes?.contentHashCode() ?: 0
        result = 31 * result + name.hashCode()
        return result
    }
}

expect class FileUtils() {
    /**
     * Converts a platform-specific file object to FileData (bytes + name)
     */
    fun fileToByteArrayAndName(photoUri: PhotoUri): FileData
}