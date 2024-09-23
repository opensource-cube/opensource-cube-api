package com.osscube.api.domain.model.repository

import com.osscube.api.domain.model.entity.OpenSource
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OpenSourceRepository : JpaRepository<OpenSource, Long> {
    fun existsByNameAndOriginUrl(name: String, originUrl: String): Boolean

    fun findByClientId(clientId: String): OpenSource?
}
