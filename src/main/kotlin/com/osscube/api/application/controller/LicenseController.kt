package com.osscube.api.application.controller

import com.osscube.api.application.response.LicenseGetResponse
import com.osscube.api.domain.service.LicenseService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/open-sources/{openSourceId}/versions/{openSourceVersionId}/licenses")
class LicenseController(
    private val licenseService: LicenseService
) {
    @GetMapping("/{licenseId}")
    @ResponseStatus(HttpStatus.OK)
    fun getLicense(@PathVariable openSourceId: String, @PathVariable openSourceVersionId: String, @PathVariable licenseId: String): LicenseGetResponse {
        val responseDto = licenseService.getLicense(openSourceId, openSourceVersionId, licenseId)
        return LicenseGetResponse.of(responseDto)
    }
}
