import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.plugin.publish)
    alias(libs.plugins.nexus.publish)
}

group = "io.github.irgaly.compose-vector"
version = libs.versions.composeVector.get()

gradlePlugin {
    website = "https://github.com/irgaly/compose-vector-plugin"
    vcsUrl = "https://github.com/irgaly/compose-vector-plugin"
    plugins {
        create("plugin") {
            id = "io.github.irgaly.compose-vector"
            displayName = "Gradle Compose Vector Plugin"
            description = "Gradle Plugin for Converting SVG file to Compose ImageVector"
            tags = listOf("compose", "svg")
            implementationClass = "io.github.irgaly.compose.vector.plugin.ComposeVectorPlugin"
        }
    }
}

dependencies {
    compileOnly(gradleKotlinDsl())
    implementation(libs.kotlin.gradle)
    implementation(libs.android.gradle)
    implementation(projects.core)
    testImplementation(libs.test.kotest.runner)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

subprojects {
    afterEvaluate {
        extensions.findByType<KotlinProjectExtension>()?.apply {
            jvmToolchain(17)
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

if (providers.environmentVariable("CI").isPresent) {
    apply(plugin = "signing")
    extensions.configure<SigningExtension> {
        useInMemoryPgpKeys(
            providers.environmentVariable("SIGNING_PGP_KEY").orNull,
            providers.environmentVariable("SIGNING_PGP_PASSWORD").orNull
        )
    }
}

nexusPublishing {
    repositories {
        sonatype {
            stagingProfileId = libs.versions.composeVector.get()
            nexusUrl = uri("https://ossrh-staging-api.central.sonatype.com/service/local/")
            snapshotRepositoryUrl =
                uri("https://central.sonatype.com/repository/maven-snapshots/")
        }
    }
}
