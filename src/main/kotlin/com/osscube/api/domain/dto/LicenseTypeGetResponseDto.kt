package com.osscube.api.domain.dto

import com.osscube.api.domain.model.entity.License

class LicenseTypeGetResponseDto(
    val id: String,
    val type: String
) {
    companion object {
        fun of(license: License) =
            LicenseTypeGetResponseDto(license.clientId, license.type)
    }
}
