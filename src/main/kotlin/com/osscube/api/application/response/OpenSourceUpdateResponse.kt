package com.osscube.api.application.response

import com.osscube.api.domain.dto.OpenSourceUpdateResponseDto

class OpenSourceUpdateResponse(
    val openSource: OpenSourceUpdateResponseDto
) {
    companion object {
        fun of(responseDto: OpenSourceUpdateResponseDto) =
            OpenSourceUpdateResponse(responseDto)
    }
}
