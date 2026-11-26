import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    kotlin("android")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

// Credenciales de firma leídas desde local.properties (no versionado).
// Si faltan, release se genera sin firmar en vez de romper el build.
val propiedadesLocales = Properties().apply {
    val archivo = rootProject.file("local.properties")
    if (archivo.exists()) archivo.inputStream().use { load(it) }
}

val rutaKeystore = propiedadesLocales.getProperty("sanaesencia.storeFile")
val claveKeystore = propiedadesLocales.getProperty("sanaesencia.storePassword")
val aliasKeystore = propiedadesLocales.getProperty("sanaesencia.keyAlias")
val claveAlias = propiedadesLocales.getProperty("sanaesencia.keyPassword")

val firmaDisponible = !rutaKeystore.isNullOrBlank() &&
    !claveKeystore.isNullOrBlank() &&
    !aliasKeystore.isNullOrBlank() &&
    !claveAlias.isNullOrBlank() &&
    rootProject.file(rutaKeystore).exists()

android {
    namespace = "cl.sanaesencia.app"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "cl.sanaesencia.app"
        minSdk = 29
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
            if (firmaDisponible) {
                signingConfig = signingConfigs.create("release") {
                    storeFile = rootProject.file(rutaKeystore!!)
                    storePassword = claveKeystore
                    keyAlias = aliasKeystore
                    keyPassword = claveAlias
                }
            }
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
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material.icons.extended)

    // ROOM
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Coil (carga de imágenes)
    implementation(libs.coil.compose)

    // Lottie (animaciones)
    implementation(libs.lottie.compose)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}