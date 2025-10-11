package ru.gghost.telegram.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Configuration
class BotConfiguration(
    @Value("\${telegram.bot.token}")
    private val token: String,
    @Value("\${telegram.bot.name}")
    private val botName: String,
    private val applicationEventPublisher: ApplicationEventPublisher,
) : TelegramLongPollingBot(token) {

    override fun onUpdateReceived(update: Update?) {
        if (update != null) {
            applicationEventPublisher.publishEvent(update)
        }
    }

    override fun getBotUsername(): String = botName

    @Bean
    fun defaultAbsSender(): DefaultAbsSender = this

    @Bean
    fun telegramBotsApi(): TelegramBotsApi {
        val bot = TelegramBotsApi(DefaultBotSession::class.java)
        bot.registerBot(this)
        return bot
    }
}