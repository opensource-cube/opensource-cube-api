package com.osscube.api.domain.dto

import com.osscube.api.domain.model.entity.OpenSourceVersion

class OpenSourceVersionGetResponseDto(
    val openSourceId: String,
    val version: String,
    val sourceUrl: String?
) {
    companion object {
        fun of(openSourceVersion: OpenSourceVersion) =
            OpenSourceVersionGetResponseDto(openSourceVersion.clientId, openSourceVersion.version, openSourceVersion.sourceUrl)
    }
}
