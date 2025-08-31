plugins {
    kotlin("jvm")
    id("io.ktor.plugin") version "3.2.3"
    application
}

application {
    mainClass.set("com.nutrisport.backend.Application")
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.serialization)
    implementation(libs.logback.classic)
    implementation(libs.ktor.client.cio)
}
