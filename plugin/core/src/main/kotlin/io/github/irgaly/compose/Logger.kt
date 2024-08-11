package io.github.irgaly.compose

interface Logger {
    fun debug(message: String)
    fun info(message: String)
    fun warn(message: String, error: Exception? = null)
    fun error(message: String, error: Exception? = null)
}
