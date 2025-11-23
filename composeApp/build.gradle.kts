import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)

    id("com.google.gms.google-services")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

val mapboxToken = localProperties.getProperty("MAPBOX_PUBLIC_TOKEN") ?: ""

// Task to generate Secrets.kt for JS
tasks.register("generateJsSecrets") {
    val outputDir = layout.buildDirectory.dir("generated/js")
    outputs.dir(outputDir)
    val token = mapboxToken
    
    doLast {
        val file = outputDir.get().file("hackatum/munichpulse/mvp/js/Secrets.kt").asFile
        file.parentFile.mkdirs()
        file.writeText("""
            package hackatum.munichpulse.mvp.js
            
            object Secrets {
                const val MAPBOX_PUBLIC_TOKEN = "$token"
            }
        """.trimIndent())
    }
}

//// Task to generate Secrets.kt for JS
//tasks.register("generateJsSecrets") {
//    val secretsDir = file("$buildDir/generated/js")
//    val secretsFile = file("$secretsDir/hackatum/munichpulse/mvp/js/Secrets.kt")
//    outputs.dir(secretsDir)
//    doLast {
//        secretsFile.parentFile.mkdirs()
//        val token = localProperties.getProperty("MAPBOX_PUBLIC_TOKEN") ?: ""
//        secretsFile.writeText("""
//            package hackatum.munichpulse.mvp.js
//
//            object Secrets {
//                const val MAPBOX_PUBLIC_TOKEN = "$token"
//            }
//        """.trimIndent())
//    }
//}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    js {
        browser()
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            kotlin.srcDir(tasks.named("generateJsSecrets").map { it.outputs.files.singleFile })
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation("com.google.firebase:firebase-auth-ktx:23.2.1")
            implementation("com.google.firebase:firebase-firestore-ktx:25.1.4")
            implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
            implementation("com.google.android.gms:play-services-auth:21.0.0")
            implementation("com.mapbox.maps:android:${libs.versions.mapbox.get()}")
            implementation("com.mapbox.extension:maps-compose:${libs.versions.mapbox.get()}")
            implementation(libs.ktor.client.okhttp)
            implementation(libs.firebase.firestore)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.multiplatform.settings)
            implementation(libs.kotlinx.datetime)
            implementation(libs.firebase.firestore)
            implementation(libs.firebase.auth)
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jsMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
            implementation(devNpm("webpack", "5.88.2"))
            implementation(devNpm("webpack-cli", "5.1.4"))
            implementation(devNpm("os-browserify", "0.3.0"))
            implementation(devNpm("path-browserify", "1.0.1"))
            implementation(npm("mapbox-gl", "3.1.2"))
        }
    }
}

android {
    namespace = "hackatum.munichpulse.mvp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "hackatum.munichpulse.mvp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "MAPBOX_PUBLIC_TOKEN", "\"${localProperties.getProperty("MAPBOX_PUBLIC_TOKEN") ?: ""}\"")
    }
    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

// Make sure js sources are generated before they are compiled
tasks.named("compileKotlinJs") {
    dependsOn(tasks.named("generateJsSecrets"))
}

// Also ensure resource processing (dev server) regenerates secrets when needed
tasks.matching { it.name == "jsProcessResources" || it.name == "jsBrowserDevelopmentRun" || it.name == "jsBrowserDistribution" }.configureEach {
    dependsOn(tasks.named("generateJsSecrets"))
}