package me.imlc

import com.google.common.util.concurrent.AbstractService
import com.google.common.util.concurrent.ServiceManager
import java.util.concurrent.TimeUnit

class App {

    private lateinit var manager: ServiceManager

    fun start() {
        manager = ServiceManager(
            listOf(
                VertxServer()
            )
        )
        manager.startAsync()
        manager.awaitHealthy(1, TimeUnit.MINUTES)
    }

    fun stop() {
        manager.stopAsync()
        manager.awaitStopped(1, TimeUnit.MINUTES)
    }
}