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
rootProject.name = "android-gradle-plugin-template"
include(":sample")
// if you want to use your plugin from Maven Plugin Portal, comment out below `includeBuild("plugin")` line.
includeBuild("plugin")
