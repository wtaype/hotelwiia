plugins {
    id("hotelwii.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.hotelwii.feature.imprimir"
}

dependencies {
    implementation(project(":core:wii"))
    implementation(project(":core:data"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:empresas"))

    implementation(libs.supabase.auth)
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.realtime)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
}
