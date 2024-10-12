package com.osscube.api.domain.model.repository

import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OpenSourceVersionRepository : JpaRepository<OpenSourceVersion, Long> {
    fun existsByOpenSourceAndVersion(openSource: OpenSource, version: String): Boolean

    fun findAllByOpenSource(openSource: OpenSource): List<OpenSourceVersion>

    fun findByOpenSourceAndClientId(openSource: OpenSource, clientId: String): OpenSourceVersion?

    fun existsByOpenSource(openSource: OpenSource): Boolean
}
