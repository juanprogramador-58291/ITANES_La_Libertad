plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.itanes_la_libertad"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.itanes_la_libertad"

        minSdk = 24
        targetSdk = 37

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    // Android base
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)

    // Room components for Java
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // Glide components for Java
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // MapLibre Native SDK
    implementation(libs.maplibre.sdk)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}
