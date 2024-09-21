package com.osscube.api.domain.dto

import com.osscube.api.domain.model.entity.OpenSource

class OpenSourceGetDto(
    val openSourceId: String,
    val name: String,
    val originUrl: String
) {
    companion object {
        fun of(openSource: OpenSource) =
            OpenSourceGetDto(openSource.clientId, openSource.name, openSource.originUrl)
    }
}
