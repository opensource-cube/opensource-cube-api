package com.osscube.api.domain.dto

import com.osscube.api.application.request.OpenSourceVersionAddNewVersionRequest

class OpenSourceVersionAddRequestDto(
    val version: String,
    val sourceUrl: String?
) {
    companion object {
        fun of(request: OpenSourceVersionAddNewVersionRequest) =
            OpenSourceVersionAddRequestDto(request.version, request.sourceUrl)
    }
}
