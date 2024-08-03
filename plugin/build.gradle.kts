plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.publish)
    signing
}

group = "io.github.irgaly.compose.vector"
version = libs.versions.composeVectorPlugin.get()

gradlePlugin {
    website = "https://github.com/irgaly/compose-vector-plugin"
    vcsUrl = "https://github.com/irgaly/compose-vector-plugin"
    plugins {
        create("plugin") {
            id = "io.github.irgaly.compose.vector"
            displayName = "Gradle Compose Vector Plugin"
            description = "Convert SVG file to Compose ImageVector"
            tags = listOf("compose")
            implementationClass = "io.github.irgaly.compose.vector.ComposeVectorPlugin"
        }
    }
}

dependencies {
    compileOnly(gradleKotlinDsl())
    implementation(libs.kotlin.gradle)
    implementation(libs.android.gradle)
    implementation(libs.xmlpull)
    implementation(libs.guava)
    implementation(libs.kotlinpoet)
    implementation(libs.batik)
}

kotlin {
    jvmToolchain(17)
}

java {
    withSourcesJar()
    withJavadocJar()
}

signing {
    useInMemoryPgpKeys(
        providers.gradleProperty("signingKey").orNull,
        providers.gradleProperty("signingPassword").orNull
    )
}

/*
// For GItHub Actions CI signing
if (providers.environmentVariable("CI").isPresent) {
    apply(plugin = "signing")
    extensions.configure<SigningExtension> {
        useInMemoryPgpKeys(
            providers.environmentVariable("SIGNING_PGP_KEY").orNull,
            providers.environmentVariable("SIGNING_PGP_PASSWORD").orNull
        )
    }
}
 */
