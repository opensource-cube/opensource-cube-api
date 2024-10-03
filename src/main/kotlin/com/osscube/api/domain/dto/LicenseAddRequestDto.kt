package com.osscube.api.domain.dto

import com.osscube.api.application.request.LicenseAddRequest
import org.springframework.web.multipart.MultipartFile

class LicenseAddRequestDto(
    val type: String,
    val file: MultipartFile
) {
    companion object {
        fun of(request: LicenseAddRequest) =
            LicenseAddRequestDto(request.type, request.file)
    }
}
