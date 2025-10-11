package ru.gghost.telegram.storage.state

interface UserStateStorage {

    fun save(userId: Long, state: String)

    fun update(userId: Long, state: String)

    fun isExists(id: Long): Boolean

    fun get(id: Long): String

}