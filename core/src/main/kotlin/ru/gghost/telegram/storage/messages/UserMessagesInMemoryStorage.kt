package ru.gghost.telegram.storage.messages

import org.springframework.stereotype.Component

@Component
class UserMessagesInMemoryStorage : UserMessagesStorage {

    private val firstMessageStorage = mutableMapOf<Long, Int>()
    private val allMessagesStorage = mutableMapOf<Long, List<Int>>()

    override fun saveFirst(userId: Long, messageId: Int): Boolean {
        return if (!firstMessageStorage.contains(userId)) {
            firstMessageStorage[userId] = messageId
            true
        } else {
            false
        }
    }

    override fun save(userId: Long, messageId: Int) {
        allMessagesStorage[userId] = allMessagesStorage.getOrDefault(userId, emptyList()) + messageId
    }

    override fun getFirst(userId: Long): Int? {
        return firstMessageStorage[userId]
    }

    override fun getAll(userId: Long): List<Int> {
        return allMessagesStorage[userId]!!
    }
}