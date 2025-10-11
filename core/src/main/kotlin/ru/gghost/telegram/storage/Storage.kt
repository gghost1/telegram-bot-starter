package ru.gghost.telegram.storage

import org.springframework.stereotype.Component
import ru.gghost.telegram.storage.messages.UserMessagesStorage
import ru.gghost.telegram.storage.state.UserStateStorage

@Component
data class Storage(
    val stateStorage: UserStateStorage,
    val messagesStorage: UserMessagesStorage
)