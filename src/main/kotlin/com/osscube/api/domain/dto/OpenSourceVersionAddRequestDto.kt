package com.osscube.api.domain.dto

import com.osscube.api.application.request.OpenSourceVersionAddRequest

class OpenSourceVersionAddRequestDto(
    val version: String,
    val sourceUrl: String?
) {
    companion object {
        fun of(request: OpenSourceVersionAddRequest) =
            OpenSourceVersionAddRequestDto(request.version, request.sourceUrl)
    }
}
