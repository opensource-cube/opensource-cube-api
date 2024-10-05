package com.osscube.api.domain.dto

import com.osscube.api.domain.model.entity.License
import com.osscube.api.domain.model.entity.OpenSourceVersion

class OpenSourceVersionAddNewVersionResponseDto(
    val id: String,
    val version: String,
    val sourceUrl: String?,
    val licenses: List<LicenseAddResponseDto>
) {
    companion object {
        fun of(openSourceVersion: OpenSourceVersion, licenses: List<License>): OpenSourceVersionAddNewVersionResponseDto {
            val licenseResponseDto = licenses.map { LicenseAddResponseDto.of(it) }
            return OpenSourceVersionAddNewVersionResponseDto(openSourceVersion.clientId, openSourceVersion.version, openSourceVersion.sourceUrl, licenseResponseDto)
        }
    }
}
