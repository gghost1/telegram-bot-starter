package ru.gghost.telegram.command

import org.springframework.context.ApplicationEventPublisher
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import ru.gghost.telegram.storage.Storage

// todo спецальное расширение для сообщений (полностью переписать создание сообщений, оставить возможность отправки кастомных)
// создание комманд

// подумать как реализовать модель отправки редактирования удаления (например чтобы стирать какую то часть сообщений)
// идея - сделать recoder чтобы удалять, сделать тип от комманды которая будет менять, или флаг на отправке
// лучше чтобы конфигурацией выставлялось изменять всегда (а удаление через recoder)
class Context(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val userStorage: Storage,
    private val defaultAbsSender: DefaultAbsSender,
    private val userId: Long,
    private val update: Update,
) {

    fun userId(): Long {
        return userId
    }

    fun update(): Update {
        return update
    }

    fun getMessage(): Message {
        return update.message
    }

    fun getCallbackQuery(): CallbackQuery {
        val callbackQuery = update.callbackQuery ?: throw NotFoundException()
        return callbackQuery
    }

    fun sendMessage(): SendMessage.SendMessageBuilder {
        return SendMessage
            .builder()
            .chatId(userId())
    }

    fun sendMessage(message: SendMessage.SendMessageBuilder, buttons: InlineKeyboardMarkup? = null): Int {
        val builtMessage = if (buttons == null) {
            message.build()
        } else {
            message
                .replyMarkup(buttons)
                .build()
        }
        var id = userStorage.messagesStorage.getFirst(userId)
        if (id != null) {
            var editMessage = EditMessageText.builder()
                .chatId(userId)
                .messageId(id)
                .text(builtMessage.text)

            if (buttons != null) {
                editMessage = editMessage
                    .replyMarkup(buttons)
            }
            try {
                defaultAbsSender.execute(editMessage.build())
            } catch (_: RuntimeException) {}

        } else {
            id = defaultAbsSender.execute(builtMessage).messageId
            userStorage.messagesStorage.saveFirst(userId, id)
        }
        try {
            defaultAbsSender.execute(
                DeleteMessages.builder()
                    .chatId(userId)
                    .messageIds(userStorage.messagesStorage.getAll(userId))
                    .build()
            )
        } catch (_: RuntimeException) {}
        return id!!
    }

    fun setState(fullState: String) {
        userStorage.stateStorage.update(userId, fullState)
    }

    fun setStateAndProcessUpdate(fullState: String) {
        setState(fullState)
        applicationEventPublisher.publishEvent(update)
    }

}