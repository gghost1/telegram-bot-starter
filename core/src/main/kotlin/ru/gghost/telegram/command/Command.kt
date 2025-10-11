package ru.gghost.telegram.command

/**
 * Base class for commands
 */
abstract class Command() {
    abstract fun processMessageUpdate(context: Context)
}