package com.osscube.api.utils

import com.fasterxml.uuid.Generators

object UUIDGenerator {
    private val uuidGenerator = Generators.timeBasedEpochRandomGenerator()

    fun generateId() =
        uuidGenerator.generate().toString()
}
