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
    indexes = [Index(name = "license_idx", columnList = "clientId")],
    uniqueConstraints = [UniqueConstraint(name = "license_unique_constraint", columnNames = ["open_source_version_id", "type"])]
)
class License(
    @ManyToOne(optional = false)
    val openSourceVersion: OpenSourceVersion,

    @Column(nullable = false)
    val type: String,

    @Column(nullable = false)
    val path: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(length = 36, nullable = false)
    val clientId: String = UUIDGenerator.generateId()
}
