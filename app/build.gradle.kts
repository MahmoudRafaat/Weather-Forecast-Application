import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.weather_forecast_app"
    compileSdk = 36

    val properties = Properties()
    val propertiesFile = project.rootProject.file("local.properties")
    if (propertiesFile.exists()) {
        properties.load(propertiesFile.inputStream())
    }

    defaultConfig {
        applicationId = "com.example.weather_forecast_app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_KEY", "\"${properties.getProperty("MAP_API_KEY") ?: ""}\"")
        buildConfigField("String", "BASE_URL", properties.getProperty("BASE_URL") ?: "\"\"")
        buildConfigField("String", "DATA_STORE_NAME", properties.getProperty("DATA_STORE_NAME") ?: "\"\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.core.splashscreen)

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.5")
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
    implementation(libs.androidx.compose.remote.creation.core)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)

    val room_version = "2.6.1"
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("com.airbnb.android:lottie-compose:6.4.0")

    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.2")

    // Osmdroid for Map
    implementation("org.osmdroid:osmdroid-android:6.1.20")

    // Material Icons Extended
    implementation("androidx.compose.material:material-icons-extended")

    // Local Unit Tests
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core.ktx)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.ext.junit.ktx)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.truth)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)

    // Instrumented Tests
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.arch.core.testing)
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.12.01"))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
