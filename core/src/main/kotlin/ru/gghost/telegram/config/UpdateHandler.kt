package ru.gghost.telegram.config

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update
import ru.gghost.telegram.state.StateService

@Service
class UpdateHandler(
    private val stateService: StateService
) {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @EventListener
    fun onApplicationEvent(update: Update) {
        coroutineScope.launch {
            stateService.process(update)
        }
    }

}