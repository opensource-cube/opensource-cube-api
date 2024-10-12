package com.osscube.api.domain.dto

import com.osscube.api.application.request.OpenSourceUpdateRequest

class OpenSourceUpdateRequestDto(
    val name: String,
    val originUrl: String
) {
    companion object {
        fun of(request: OpenSourceUpdateRequest) =
            OpenSourceUpdateRequestDto(request.name, request.originUrl)
    }
}
