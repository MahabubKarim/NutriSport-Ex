package com.nutrisport.data

import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.logging.*

actual fun createHttpClient(): HttpClient = HttpClient(Darwin) {
    install(Logging) { level = LogLevel.INFO }
}
