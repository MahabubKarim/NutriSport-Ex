package com.nutrisport.shared.util

import androidx.datastore.preferences.core.edit
import com.mmk.kmpauth.google.GoogleUser
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable

@Serializable
data class StoredGoogleUser(
    val id: String,
    val email: String?,
    val displayName: String?,
    val accessToken: String?
)

interface PreferenceUtils {
    suspend fun setGoogleToken(token: String)

    suspend fun getGoogleToken(): String?
    suspend fun clearGoogleToken()
    suspend fun saveGoogleUser(user: GoogleUser?)
    suspend fun getGoogleUser(): GoogleUser?
    suspend fun clearGoogleUser()
}
