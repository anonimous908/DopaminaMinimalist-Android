plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.protas.dopaminaminimalist"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.protas.dopaminaminimalist"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
    buildFeatures {
        compose = true
    }
    // Evita que el compilador elimine las librerías nativas necesarias
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Agrega esto para tener acceso a TODOS los iconos (Extended)
    implementation("androidx.compose.material:material-icons-extended:1.7.6")
    // AGREGA ESTAS LÍNEAS (LiteRT 1.4.1 o superior)
    // --- LiteRT (Compatible con Android 15 / 16 KB) ---
    implementation("com.google.ai.edge.litert:litert:1.4.1")
    implementation("com.google.ai.edge.litert:litert-api:1.4.1")
    // implementation("com.google.ai.edge.litert:litert-gpu:1.4.1") // Descomenta solo si usas GPU

    // BORRA ESTA LÍNEA (No existe aún):
    // implementation("com.google.ai.edge.litert:litert-select-tf-ops:1.4.1")
    // ¡CRÍTICO! Tu modelo usa GRU/LSTM, necesitas esta librería para las "Select Ops":


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

    // Navegación entre pantallas
    implementation("androidx.navigation:navigation-compose:2.7.7")
    // Memoria local para guardar que ya aceptó las políticas
    implementation("androidx.datastore:datastore-preferences:1.0.0")

}




