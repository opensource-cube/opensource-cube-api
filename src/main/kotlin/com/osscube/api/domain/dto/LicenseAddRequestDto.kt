package com.osscube.api.domain.dto

import org.springframework.web.multipart.MultipartFile

class LicenseAddRequestDto(
    val type: String,
    val file: MultipartFile
)
