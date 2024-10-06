package com.osscube.api.domain.dto

import com.osscube.api.domain.model.entity.License

class LicenseGetResponseDto(
    val id: String,
    val type: String
) {
    companion object {
        fun of(license: License) =
            LicenseGetResponseDto(license.clientId, license.type)
    }
}
