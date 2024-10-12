package com.osscube.api.domain.dto

class OpenSourceUpdateResponseDto(
    val id: String,
    val name: String,
    val originUrl: String
) {
    companion object {
        fun of(openSourceId: String, requestDto: OpenSourceUpdateRequestDto) =
            OpenSourceUpdateResponseDto(openSourceId, requestDto.name, requestDto.originUrl)
    }
}
