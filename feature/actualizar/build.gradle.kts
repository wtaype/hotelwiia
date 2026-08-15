plugins {
    id("hotelwii.android.feature")
}

android {
    namespace = "com.hotelwii.feature.actualizar"
}

dependencies {
    implementation(project(":core:wii"))
    implementation(project(":core:data"))
}
