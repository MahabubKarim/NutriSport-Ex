package com.nutrisport.shared.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mmk.kmpauth.google.GoogleUser
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore("user_prefs")

class AndroidPreferenceUtils(private val context: Context) : PreferenceUtils {

    private val GOOGLE_TOKEN_KEY = stringPreferencesKey("google_token")
    private val GOOGLE_USER_KEY = stringPreferencesKey("google_user")

    override suspend fun setGoogleToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[GOOGLE_TOKEN_KEY] = token
        }
    }

    override suspend fun getGoogleToken(): String? {
        val prefs = context.dataStore.data.first()
        return prefs[GOOGLE_TOKEN_KEY]
    }

    override suspend fun clearGoogleToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(GOOGLE_TOKEN_KEY)
        }
    }

    override suspend fun saveGoogleUser(user: GoogleUser?) {
        val stored = StoredGoogleUser(
            id = user?.idToken ?: "",
            email = user?.email,
            displayName = user?.displayName,
            accessToken = user?.accessToken
        )
        context.dataStore.edit { prefs ->
            prefs[GOOGLE_USER_KEY] = Json.encodeToString(StoredGoogleUser.serializer(), stored)
        }
    }

    override suspend fun getGoogleUser(): GoogleUser? {
        val prefs = context.dataStore.data.first()
        val json: String = prefs[GOOGLE_USER_KEY] as String
        return try {
            val stored = Json.decodeFromString(StoredGoogleUser.serializer(), json)
            GoogleUser(
                idToken = stored.id,
                email = stored.email,
                displayName = stored.displayName ?: "",
                accessToken = stored.accessToken
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun clearGoogleUser() {
        context.dataStore.edit { prefs ->
            prefs.remove(GOOGLE_USER_KEY)
        }
    }
}
