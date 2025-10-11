package ru.gghost.telegram.storage.state

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import ru.gghost.telegram.storage.state.UserStateInMemoryStateStorage.Companion.STATE_STORAGE
import ru.gghost.telegram.storage.exception.UserAlreadyExistsException
import ru.gghost.telegram.storage.exception.UserNotFoundException

@Service
@Qualifier(STATE_STORAGE)
class UserStateInMemoryStateStorage: UserStateStorage {

    companion object {
        const val STATE_STORAGE = "STATE_STORAGE"
    }

    private val storage = mutableMapOf<Long, String>()

    override fun save(userId: Long, state: String) {
        if (storage.contains(userId)) {
            throw UserAlreadyExistsException("User with id $userId already exists")
        }
        storage[userId] = state
    }

    override fun update(userId: Long, state: String) {
        if (!storage.contains(userId)) {
            throw UserNotFoundException("User with id $userId is not found")
        }
        storage[userId] = state
    }

    override fun isExists(id: Long): Boolean {
        return storage.contains(id)
    }

    override fun get(id: Long): String {
        return if (storage.contains(id)) {
            storage[id]!!
        } else {
            throw UserNotFoundException("User with id $id is not found")
        }
    }
}