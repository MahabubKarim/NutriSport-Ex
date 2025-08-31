import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "data"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            // OkHttp engine for Ktor Client
            implementation("io.ktor:ktor-client-okhttp:3.2.3")
            // Logging plugin for Ktor Client
            implementation("io.ktor:ktor-client-logging:3.2.3")
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.android.client)
        }

        iosMain.dependencies {
            // OkHttp engine for Ktor Client
            implementation("io.ktor:ktor-client-okhttp:3.2.3")
            // Logging plugin for Ktor Client
            implementation("io.ktor:ktor-client-logging:3.2.3")
            implementation(libs.ktor.darwin.client)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.serialization)

            implementation(libs.firebase.firestore)
            implementation(libs.firebase.storage)
            implementation(libs.auth.firebase.kmp)

            implementation(libs.auth.kmp)

            implementation(libs.google.auth)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.logging)
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.coroutines.core)

            implementation(project(":core"))
            implementation(project(":shared"))
            implementation(project(":domain"))
        }

        jvmMain.dependencies {
            implementation(project(":backend"))
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.nutrisport.data"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

