import java.util.Properties

plugins {
    id("hotelwii.android.library")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        load(localFile.inputStream())
    }
}

android {
    namespace = "com.hotelwii.core.data"

    buildFeatures {
        compose = true
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "R2_BUCKET", "\"${localProperties.getProperty("R2_BUCKET", "hotelwii-docs")}\"")
        buildConfigField("String", "R2_ACCESS_KEY_ID", "\"${localProperties.getProperty("R2_ACCESS_KEY_ID", "")}\"")
        buildConfigField("String", "R2_SECRET_ACCESS_KEY", "\"${localProperties.getProperty("R2_SECRET_ACCESS_KEY", "")}\"")
        buildConfigField("String", "R2_ENDPOINT", "\"${localProperties.getProperty("R2_ENDPOINT", "")}\"")
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)

    // Supabase
    implementation(libs.supabase.auth)
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.realtime)

    // Ktor
    implementation(libs.ktor.client.android)
    implementation(libs.kotlinx.serialization.json)
}
