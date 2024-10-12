package com.osscube.api.config

import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
open class TestContainers {
    companion object {
        @Container
        @JvmStatic
        private val mysql = MySQLContainer("mysql:8")
    }
}
