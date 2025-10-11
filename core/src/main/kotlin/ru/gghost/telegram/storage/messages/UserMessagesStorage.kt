package ru.gghost.telegram.storage.messages

interface UserMessagesStorage {

    fun saveFirst(userId: Long, messageId: Int): Boolean

    fun save(userId: Long, messageId: Int)

    fun getFirst(userId: Long): Int?

    fun getAll(userId: Long): List<Int>

}