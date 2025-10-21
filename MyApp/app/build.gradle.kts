plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20" // Use your current Kotlin version
}

android {
    namespace = "com.composelab.myapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.composelab.myapp"
        minSdk = 32
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {


    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    // 👇 FIX 3: Kotlinx Serialization for JSON parsing
    // Unresolved reference 'json' (if you were using Json{...}) and the Serializable annotation
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // OkHttp logging interceptor (Good for debugging network calls)
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // 👇 FIX 4: Jetpack Compose ViewModel integration
    // Unresolved reference 'viewModel'
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Standard Coroutines for ViewModelScope
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // For the .toMediaType() extension function used with Retrofit
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}