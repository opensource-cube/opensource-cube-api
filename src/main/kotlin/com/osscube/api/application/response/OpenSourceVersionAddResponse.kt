package com.osscube.api.application.response

import com.osscube.api.domain.dto.OpenSourceVersionAddNewVersionResponseDto

class OpenSourceVersionAddResponse(
    val openSourceVersion: OpenSourceVersionAddNewVersionResponseDto
) {
    companion object {
        fun of(responseDto: OpenSourceVersionAddNewVersionResponseDto) =
            OpenSourceVersionAddResponse(responseDto)
    }
}
