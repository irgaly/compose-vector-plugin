import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.compose.compiler) apply false
}

subprojects {
    afterEvaluate {
        extensions.findByType<KotlinProjectExtension>()?.apply {
            jvmToolchain(17)
        }
    }
}
