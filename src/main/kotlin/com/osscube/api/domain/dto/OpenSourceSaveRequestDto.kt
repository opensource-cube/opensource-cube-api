package com.osscube.api.domain.dto

import com.osscube.api.application.request.OpenSourceSaveRequest

class OpenSourceSaveRequestDto(
    val name: String,
    val originUrl: String
) {
    companion object {
        fun of(request: OpenSourceSaveRequest) =
            OpenSourceSaveRequestDto(request.name, request.originUrl)
    }
}
