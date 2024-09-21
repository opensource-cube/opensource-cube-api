package com.osscube.api.domain.dto

import org.springframework.web.multipart.MultipartFile

class OpenSourceVersionAddRequestDto(
    val openSourceId: String,
    val version: String,
    val sourceFile: MultipartFile?
)
