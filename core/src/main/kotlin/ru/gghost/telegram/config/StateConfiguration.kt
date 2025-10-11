package ru.gghost.telegram.config

import mu.KotlinLogging
import org.springframework.aop.support.AopUtils
import org.springframework.beans.factory.SmartInitializingSingleton
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import ru.gghost.telegram.command.Command
import ru.gghost.telegram.state.State
import ru.gghost.telegram.state.StateTree

@Component
class StateConfiguration(
    private val applicationContext: ApplicationContext,
) : SmartInitializingSingleton {

    companion object {
        private val clazz = State::class.java
        private val log = KotlinLogging.logger {  }
    }

    override fun afterSingletonsInstantiated() {
        val beansWithAnnotation = applicationContext.getBeansWithAnnotation(clazz)
        log.info { "Beans with annotation $clazz: $beansWithAnnotation" }
        beansWithAnnotation.values.forEach { bean ->
            val targetClass = AopUtils.getTargetClass(bean)
            if (targetClass.isAnnotationPresent(clazz)) {
                val annotation = targetClass.getAnnotation(clazz)

                if (bean is Command) {
                    StateTree.register(annotation.name, bean)
                } else {
                    throw IllegalArgumentException("Annotation State is applied only for classes extending Command. ${bean.javaClass.name}")
                }
            }
        }
        log.info { "Commands tree:\n${StateTree.getBaseStage()}" }
    }

}