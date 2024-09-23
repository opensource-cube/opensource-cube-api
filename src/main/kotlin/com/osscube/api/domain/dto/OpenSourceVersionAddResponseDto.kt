package com.osscube.api.domain.dto

import com.osscube.api.domain.model.entity.OpenSourceVersion

class OpenSourceVersionAddResponseDto(
    val id: String,
    val version: String,
    val sourceUrl: String?
) {
    companion object {
        fun of(openSourceVersion: OpenSourceVersion) =
            OpenSourceVersionAddResponseDto(openSourceVersion.clientId, openSourceVersion.version, openSourceVersion.sourceUrl)
    }
}
