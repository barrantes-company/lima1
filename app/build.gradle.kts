plugins {
    id("com.android.application")
}

android {
    namespace = "co.barrantes.lima1"
    compileSdk = 35

    defaultConfig {
        applicationId = "co.barrantes.lima1"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}
