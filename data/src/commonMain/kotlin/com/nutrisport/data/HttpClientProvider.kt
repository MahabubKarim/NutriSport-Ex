package com.nutrisport.data


import io.ktor.client.*

/**
 * Expect a platform-specific HttpClient provider (OkHttp on Android, Darwin on iOS).
 */
expect fun createHttpClient(): HttpClient