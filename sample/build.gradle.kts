plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.sampleplugin)
}

android {
    namespace = "org.sample.app"
    compileSdk = 34
    defaultConfig {
        applicationId = "org.sample.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }
}

kotlin {
    jvmToolchain(17)
}

greeting {
    who = "mate"
}

dependencies {
    implementation(dependencies.platform(libs.compose.bom))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle)
    implementation(libs.bundles.compose)
}
