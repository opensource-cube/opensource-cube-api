package com.osscube.api.application.response

import com.osscube.api.domain.dto.LicenseGetResponseDto

class LicenseGetResponse(
    val license: LicenseGetResponseDto
) {
    companion object {
        fun of(licenseGetResponseDto: LicenseGetResponseDto) = LicenseGetResponse(licenseGetResponseDto)
    }
}
