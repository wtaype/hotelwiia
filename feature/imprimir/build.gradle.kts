plugins {
    id("hotelwii.android.feature")
}

android {
    namespace = "com.hotelwii.feature.imprimir"
}

dependencies {
    implementation(project(":core:wii"))
    implementation(project(":core:data"))
}
