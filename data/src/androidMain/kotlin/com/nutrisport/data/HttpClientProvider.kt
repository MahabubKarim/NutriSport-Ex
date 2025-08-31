package com.nutrisport.data

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.logging.*

actual fun createHttpClient(): HttpClient = HttpClient(OkHttp) {
    install(Logging) { level = LogLevel.INFO }
    // configure timeouts or interceptors if required
}
