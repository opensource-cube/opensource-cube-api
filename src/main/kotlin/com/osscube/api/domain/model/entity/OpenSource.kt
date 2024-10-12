package com.osscube.api.domain.model.entity

import com.osscube.api.domain.dto.OpenSourceSaveRequestDto
import com.osscube.api.utils.UUIDGenerator
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    indexes = [Index(name = "open_source_idx", columnList = "clientId")],
    uniqueConstraints = [UniqueConstraint(name = "open_source_unique_constraint", columnNames = ["name", "originUrl"])]
)
class OpenSource(
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var originUrl: String
) {
    @Column(length = 36, nullable = false)
    val clientId: String = UUIDGenerator.generateId()

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    companion object {
        fun of(dto: OpenSourceSaveRequestDto) =
            OpenSource(dto.name, dto.originUrl)
    }

    fun update(name: String, originUrl: String) {
        this.name = name
        this.originUrl = originUrl
    }
}
