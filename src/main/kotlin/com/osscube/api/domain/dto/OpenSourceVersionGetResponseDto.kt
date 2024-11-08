package com.osscube.api.domain.dto

class OpenSourceVersionGetResponseDto(
    val id: String,
    val version: String,
    val sourceUrl: String?,
    val licenses: List<LicenseTypeGetResponseDto>
) {
    companion object {
        fun of(id: String, version: String, sourceUrl: String?, licenses: List<LicenseTypeGetResponseDto>) =
            OpenSourceVersionGetResponseDto(id, version, sourceUrl, licenses)
    }
}
