plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.composeVector)
}

android {
    namespace = "io.github.irgaly.compose.vector.sample"
    compileSdk = 34
    defaultConfig {
        applicationId = "io.github.irgaly.compose.vector.sample"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }
    buildFeatures {
        compose = true
    }
    // compose-vector-pluginの変換結果確認ディレクトリ
    sourceSets.findByName(SourceSet.MAIN_SOURCE_SET_NAME)?.kotlin?.srcDir(
        layout.buildDirectory.dir("test").get().asFile.path
    )
}

kotlin {
    jvmToolchain(17)
}

composeVector {
    packageName = "io.github.irgaly.compose.vector.sample.image"
}

dependencies {
    implementation(dependencies.platform(libs.compose.bom))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle)
    implementation(libs.bundles.compose)
}
