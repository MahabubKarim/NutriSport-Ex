package com.nutrisport.shared.util

import com.mmk.kmpauth.google.GoogleUser
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

class IOSPreferenceUtils : PreferenceUtils {

    private val defaults = NSUserDefaults.standardUserDefaults
    private val GOOGLE_USER_KEY = "google_user"

    override suspend fun saveGoogleUser(user: GoogleUser) {
        val stored = StoredGoogleUser(
            id = user.idToken,
            email = user.email,
            displayName = user.displayName,
            idToken = user.idToken,
            accessToken = user.accessToken
        )
        val json = Json.encodeToString(StoredGoogleUser.serializer(), stored)
        defaults.setObject(json, forKey = GOOGLE_USER_KEY)
    }

    override suspend fun getGoogleUser(): GoogleUser? {
        val json = defaults.stringForKey(GOOGLE_USER_KEY) ?: return null
        return try {
            val stored = Json.decodeFromString(StoredGoogleUser.serializer(), json)
            GoogleUser(
                email = stored.email,
                displayName = stored.displayName!!,
                idToken = stored.id,
                accessToken = stored.accessToken
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun clearGoogleUser() {
        defaults.removeObjectForKey(GOOGLE_USER_KEY)
    }
}
