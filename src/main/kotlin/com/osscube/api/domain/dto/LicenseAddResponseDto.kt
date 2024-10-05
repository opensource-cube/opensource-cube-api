package com.osscube.api.domain.dto

import com.osscube.api.domain.model.entity.License

class LicenseAddResponseDto(
    val id: String,
    val type: String
) {
    companion object {
        fun of(license: License) =
            LicenseAddResponseDto(license.clientId, license.type)
    }
}
