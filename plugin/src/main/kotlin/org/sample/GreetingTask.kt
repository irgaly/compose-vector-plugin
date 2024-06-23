package org.sample

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

abstract class GreetingTask : DefaultTask() {
    @get:Optional
    @get:Input
    abstract val who: Property<String>

    @TaskAction
    fun greet() {
        val w = who.getOrElse("mate")
        println("Hi $w!!!")
    }
}
