package com.osscube.api.domain.dto

class OpenSourceVersionGetResponseDto(
    val id: String,
    val version: String,
    val sourceUrl: String?,
    val licenses: List<LicenseGetResponseDto>
) {
    companion object {
        fun of(id: String, version: String, sourceUrl: String?, licenses: List<LicenseGetResponseDto>) =
            OpenSourceVersionGetResponseDto(id, version, sourceUrl, licenses)
    }
}
