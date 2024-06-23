package org.sample

import org.gradle.api.Plugin
import org.gradle.api.Project

class GreetingPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("greeting", GreetingExtension::class.java)
        val task = target.tasks.register("greeting", GreetingTask::class.java)
        target.afterEvaluate {
            task.configure { target ->
                target.who.set(extension.who)
            }
        }
    }
}
