package com.osscube.api.domain.dto

import com.osscube.api.domain.model.entity.OpenSource

class OpenSourceSaveResponseDto(
    val clientId: String,
    val name: String,
    val originUrl: String
) {
    constructor(openSource: OpenSource) : this(openSource.clientId, openSource.name, openSource.originUrl)

    companion object {
        fun of(openSource: OpenSource) =
            OpenSourceSaveResponseDto(openSource)
    }
}
