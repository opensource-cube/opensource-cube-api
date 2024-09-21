package com.osscube.api.domain.model.repository

import com.osscube.api.domain.model.entity.OpenSourceVersion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OpenSourceVersionRepository : JpaRepository<OpenSourceVersion, Long>
