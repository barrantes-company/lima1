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
        versionCode = 3
        versionName = "1.0.2"
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
