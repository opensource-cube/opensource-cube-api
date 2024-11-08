package com.osscube.api.domain.model.repository

import com.osscube.api.domain.model.entity.License
import com.osscube.api.domain.model.entity.OpenSourceVersion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LicenseRepository : JpaRepository<License, Long> {
    fun findByOpenSourceVersionAndClientId(openSourceVersion: OpenSourceVersion, clientId: String): License?
}
