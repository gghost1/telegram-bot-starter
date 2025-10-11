package ru.gghost.telegram.state

import org.springframework.stereotype.Component

/**
 * Main annotation for commands in the state. The annotated command will be registered in [ru.gghost.telegram.state.StateService] automatically
 *
 * Command - the class extending base [ru.telegram.core.command.Command] class and annotated with the [State] annotation
 *
 * State name - string line with stages split by '_'
 *
 * For example start command in the bot has `BASE` state and class StartCommand. It will look like this:
 * ```
 * @State("BASE")
 * class StartCommand(...) : Command(...) {...}
 * ```
 * The next state will be `BASE_ACCOUNT` to see account
 * ```
 * @State("BASE_ACCOUNT")
 * class AccountCommand(...) : Command(...) {...}
 * ```
 *
 * @see [StateService]
 * @see [ru.gghost.telegram.core.command.Command]
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class State(
    val name: String
)