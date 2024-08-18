enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.8.0")
}
rootProject.name = "compose-vector-plugin"
include(":sample:android")
include(":sample:multiplatform")
//include(":sample:jvm-library")
includeBuild("plugin")
