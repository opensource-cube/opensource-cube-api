package com.osscube.api.config

import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container

open class TestContainers {
    companion object {
        @Container
        @JvmStatic
        private val mysql = MySQLContainer("mysql:8")
            .waitingFor(Wait.forHealthcheck())
    }
}
