# Android Gradle Plugin Template

Template repository for modern Android Gradle Plugin Project.

* Kotlin 2.0.0
* Android Gradle Plugin 8.4.0
  * Sample App's compileSdk = 33 (Android 12)
  * Sample App's minSdk = 26 (Android 8.0)
* Gradle 8.7
  * Version Catalog
  * Kotlin DSL (*.kts)
  * pluginManagement / dependencyResolutionManagement (settings.gradle.kts)
  * Composite Build
  * Gradle Plugin Publish Plugin
  * Gradle Signing Plugin

## Publish Plugin

docs: https://docs.gradle.org/8.7/userguide/publishing_gradle_plugins.html

* register Maven Plugin Portal Account
  * https://plugins.gradle.org/user/register
  * Using login with Github account is recommended to use `io.github.{user}.{plugin}` plugin id.

Set your API Key and signing key to gradle.properties, or specify it as command line arguments.

`~/.gradle/gradle.properties`

```properties
gradle.publish.key=...
gradle.publish.secret=...
signingKey="-----BEGIN PGP PRIVATE KEY BLOCK-----\
\
...\
-----END PGP PRIVATE KEY BLOCK-----\
"
signingPassword=...
```

Configure your plugin publications.

Plugin id must have your owned domain or `io.github.{user}`. see this
document: https://plugins.gradle.org/docs/publish-plugin#approval

`plugin/build.gradle.kts`

```kotlin
group =
  "io.github.{user}.{plugin name}" // maven artifact groupId, it's recommended to same as plugin id.
version = "0.1.0"

gradlePlugin {
  website = "https://github.com/example/example"
  vcsUrl = "https://github.com/example/example"
  plugins {
    create("plugin") { // unique name in your config
      id = "io.github.{user}.{plugin name}" // plugin id
      displayName = "Sample Plugin"
      description = "A Sample Plugin"
      tags = listOf("example")
      implementationClass = "org.sample.GreetingPlugin"
    }
  }
}
```

publish command

```shell
./gradle :plugin:publishPlugins
```

or command with API Key args

```shell
./gardlew :plugin:publishPlugins -Pgradle.publish.key=<key> -Pgradle.publish.secret=<secret>
```
