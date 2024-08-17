import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.batik)
    implementation(libs.kotlinpoet)
    testImplementation(libs.test.kotest.runner)
    testImplementation(libs.test.kotest.assertions)

    // for temporary sample code
    implementation(libs.xmlpull)
    implementation(libs.guava)
}

java {
    withSourcesJar()
    withJavadocJar()
}

val dokkaJavadoc by tasks.getting(DokkaTask::class)
val javadocJar by tasks.getting(Jar::class) {
    dependsOn(dokkaJavadoc)
    from(dokkaJavadoc.outputDirectory)
}

signing {
    useInMemoryPgpKeys(
        providers.environmentVariable("SIGNING_PGP_KEY").orNull,
        providers.environmentVariable("SIGNING_PGP_PASSWORD").orNull
    )
    if (providers.environmentVariable("CI").isPresent) {
        sign(extensions.getByType<PublishingExtension>().publications)
    }
}

group = "io.github.irgaly.compose.vector"
version = libs.versions.composeVector.get()

publishing {
    publications {
        create<MavenPublication>("mavenCentral") {
            from(components["java"])
            artifactId = "compose-vector"
            pom {
                name = artifactId
                description = "Convert SVG file to Compose ImageVector"
                url = "https://github.com/irgaly/compose-vector-plugin"
                developers {
                    developer {
                        id = "irgaly"
                        name = "irgaly"
                        email = "irgaly@gmail.com"
                    }
                }
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                scm {
                    connection = "git@github.com:irgaly/compose-vector-plugin.git"
                    developerConnection =
                        "git@github.com:irgaly/compose-vector-plugin.git"
                    url = "https://github.com/irgaly/compose-vector-plugin"
                }
            }
        }
    }
}
