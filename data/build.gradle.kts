plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.mortgage.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.kotlinx.coroutines.android)
    ksp(libs.room.compiler)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
    testImplementation(kotlin("test"))
    androidTestImplementation(libs.androidx.test.ext.junit)
}
