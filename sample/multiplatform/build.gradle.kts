plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.composeVector)
}

kotlin {
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "io.github.irgaly.compose.vector.sample.MainKt"
    }
}

composeVector {
    packageName = "io.github.irgaly.compose.vector.sample.image"
}
