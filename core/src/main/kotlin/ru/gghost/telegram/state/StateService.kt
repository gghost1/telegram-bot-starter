package ru.gghost.telegram.state

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.objects.Update
import ru.gghost.telegram.command.Context
import ru.gghost.telegram.command.ErrorCommand
import ru.gghost.telegram.command.NotFoundException
import ru.gghost.telegram.exception.UserIdNotFoundException
import ru.gghost.telegram.storage.Storage
import ru.gghost.telegram.storage.exception.UserNotFoundException


@Service
class StateService(
    private val stateTree: StateTree = StateTree.getBaseStage(),
    private val errorCommand: ErrorCommand,
    private val userStorage: Storage,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val defaultAbsSender: DefaultAbsSender,
    @Value("\${telegram.bot.startState}")
    private val startState: String
) {

    companion object {
        private val log = KotlinLogging.logger {  }
    }

    fun process(update: Update) {
        try {
            if (update.message != null) {
                userStorage.messagesStorage.save(update.message.chatId, update.message.messageId)
            }
            val id = getIdFromUpdate(update)
            val context = Context(
                applicationEventPublisher,
                userStorage,
                defaultAbsSender,
                id,
                update
            )
            val state = try {
                userStorage.stateStorage.get(id)
            } catch (ex: UserNotFoundException) {
                userStorage.stateStorage.save(id, startState)
                startState
            }
            try {
                val command = stateTree.getCommand(state)
                command!!.processMessageUpdate(context)
            } catch (_: NotFoundException) {
            } catch (ex: RuntimeException) {
                log.error(ex) { "Error processing update for user $id\n$update" }
                errorCommand.processMessageUpdate(context)
            }
        } catch (ex: UserIdNotFoundException) {
            log.error(ex) { update }
        }

    }

    private fun getIdFromUpdate(update: Update): Long {
        if (update.hasMessage()) {
            return update.message.chatId
        } else if (update.hasCallbackQuery()) {
            return update.callbackQuery.from.id
        }
        throw UserIdNotFoundException("Can not find id from update")
    }

}