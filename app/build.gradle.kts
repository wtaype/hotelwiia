plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.hotelwii.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.hotelwii.app"
        minSdk = 29
        targetSdk = 36
        versionCode = 20400
        versionName = "2.4.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystoreFile = rootProject.file("hotelwii-release.jks")
            if (keystoreFile.exists()) {
                storeFile = keystoreFile
                storePassword = "hotelwiipassword"
                keyAlias = "hotelwii"
                keyPassword = "hotelwiipassword"
                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
                enableV4Signing = true
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    implementation(project(":core:wii"))
    implementation(project(":core:data"))
    implementation(project(":feature:hola"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:cuenta"))
    implementation(project(":feature:empresas"))
    implementation(project(":feature:recepcion"))
    implementation(project(":feature:actualizar"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
}
