package com.osscube.api.domain.dto

import com.osscube.api.domain.model.entity.OpenSourceVersion

class OpenSourceVersionsGetResponseDto(
    val id: String,
    val version: String,
    val sourceUrl: String?
) {
    companion object {
        fun of(openSourceVersion: OpenSourceVersion) =
            OpenSourceVersionsGetResponseDto(openSourceVersion.clientId, openSourceVersion.version, openSourceVersion.sourceUrl)
    }
}
