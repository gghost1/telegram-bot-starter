package ru.gghost.telegram.state

import mu.KotlinLogging
import ru.gghost.telegram.command.Command

/**
 * Class contains tree of states with corresponding commands
 */
class StateTree private constructor(
    private val name: String = "",
    private val next: MutableMap<String, StateTree> = mutableMapOf(),
    private var command: Command? = null,
    private val prev: StateTree? = null
) {

    companion object {

        private var baseStage: StateTree? = null
        private val log = KotlinLogging.logger {  }

        /**
         * initialize new state with the command in baseStage
         */
        fun register(fullState: String, command: Command) {
            val states = fullState.uppercase().split("_")

            var stateNode = getBaseStage()
            for (state in states) {
                if (stateNode.next.contains(state)) {
                    stateNode = stateNode.next[state]!!
                } else {
                    val newStateNode = StateTree(
                        name = state,
                        prev = stateNode
                    )
                    stateNode.next[state] = newStateNode
                    stateNode = newStateNode
                }
            }
            if (stateNode.command == null) {
                stateNode.command = command
            } else {
                throw IllegalArgumentException("Command with state $fullState is duplicated. ${stateNode.command!!.javaClass.name}, ${command.javaClass.name}")
            }
        }

        /**
         * return singleton instance of the first node of the StateTree
         */
        fun getBaseStage(): StateTree {
            if (baseStage == null) {
                baseStage = StateTree()
            }
            return baseStage!!
        }
    }

    // TODO test this recursive method
    fun getCommand(fullState: String, stateName: String? = null): Command? {
        val states = fullState.uppercase().split("_")

        if (fullState.isEmpty() && stateName == name) {
            if (command != null) {
                return command
            } else {
                log.error { "Command with state $fullState is not found." }
                return null
            }
        }

        if (next.contains(states[0])) {
            val stateList = states.stream().skip(1).toList()

            val state = if (stateList.size == 1) stateList[0] else stateList.joinToString("_")
            return next[states[0]]!!.getCommand(
                state,
                states[0]
                )
        } else {
            log.error { "Command with state $fullState is not found." }
            return null
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("|$name")
        next.forEach { (_, value) ->
            builder.append(value.toString(1))
        }
        return builder.toString()
    }

    private fun toString(nested: Int): String {
        val nesting = "----|".repeat(nested - 1)
        val builder = StringBuilder()
        builder.append("\n|$nesting$name")
        if (next.isNotEmpty()) {
            next.forEach { (_, value) ->
                builder.append(value.toString(nested + 1))
            }
        }
        return builder.toString()
    }

}