package com.osscube.api.domain.model.entity

import com.osscube.api.utils.UUIDGenerator
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    indexes = [Index(name = "open_source_version_idx", columnList = "client_id")],
    uniqueConstraints = [UniqueConstraint(name = "open_source_version_unique_constraint", columnNames = ["open_source_id", "version"])]
)
class OpenSourceVersion(
    @ManyToOne(optional = false)
    val openSource: OpenSource,

    @Column(nullable = false)
    val version: String,

    val sourceUrl: String?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(length = 36, nullable = false)
    val clientId: String = UUIDGenerator.generateId()
}
