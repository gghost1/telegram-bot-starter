package ru.gghost.telegram.command

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ErrorCommand(
    @Value("\${telegram.token:Internal error}")
    private val message: String
) : Command() {
    override fun processMessageUpdate(context: Context) {
        context.sendMessage(
            context.sendMessage()
                .text(message)
        )
    }
}