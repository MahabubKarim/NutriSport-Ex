import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.google.services)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(compose.material3)
            implementation(libs.google.material)
            implementation(libs.splash.screen)

            implementation(libs.koin.android)

            implementation(libs.firebase.auth.ktx)
            implementation(libs.firebase.common.ktx)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(libs.google.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.auth.kmp)
            implementation(libs.auth.firebase.kmp)
            implementation(libs.koin.compose)

            implementation(libs.google.auth)

            implementation(project(":core"))
            implementation(project(":shared"))
            implementation(project(":domain"))
            implementation(project(":data"))
            implementation(project(":di"))
            implementation(project(":navigation"))
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.mmk.nutrisport"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.mmk.nutrisport"
        minSdk = libs.versions.android.minSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {
        getByName("debug")

        // Only configure release signing if properties exist
        create("release") {
            val keystoreFile = project.findProperty("RELEASE_STORE_FILE") as String?
            val keystorePassword = project.findProperty("RELEASE_STORE_PASSWORD") as String?
            val keyAlias = project.findProperty("RELEASE_KEY_ALIAS") as String?
            val keyPassword = project.findProperty("RELEASE_KEY_PASSWORD") as String?

            if (keystoreFile != null &&
                keystorePassword != null &&
                keyAlias != null &&
                keyPassword != null
            ) {
                storeFile = file(keystoreFile)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            } else {
                // fallback: don't assign signing config, Gradle will complain only on release builds
                println("⚠️ No release signing config found. Make sure to define it in gradle.properties for release builds.")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

