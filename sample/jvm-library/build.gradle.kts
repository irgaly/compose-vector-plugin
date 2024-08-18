plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm {
        mainRun {
            mainClass = "io.github.irgaly.compose.vector.sample.MainKt"
        }
    }
    sourceSets {
        jvmMain {
            dependencies {
                implementation(libs.composeVector)
            }
        }
    }
}
