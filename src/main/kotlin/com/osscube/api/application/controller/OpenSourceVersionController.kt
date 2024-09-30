package com.osscube.api.application.controller

import com.osscube.api.application.request.OpenSourceVersionAddRequest
import com.osscube.api.application.response.OpenSourceVersionAddResponse
import com.osscube.api.application.response.OpenSourceVersionGetResponse
import com.osscube.api.application.response.OpenSourceVersionsGetResponse
import com.osscube.api.domain.dto.OpenSourceVersionAddRequestDto
import com.osscube.api.domain.service.OpenSourceVersionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/open-sources/{openSourceId}/versions")
class OpenSourceVersionController(
    private val openSourceVersionService: OpenSourceVersionService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addNewVersion(@PathVariable openSourceId: String, @RequestBody request: OpenSourceVersionAddRequest): OpenSourceVersionAddResponse {
        val responseDto = openSourceVersionService.addNewVersion(openSourceId, OpenSourceVersionAddRequestDto.of(request))
        return OpenSourceVersionAddResponse(responseDto)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getVersions(@PathVariable openSourceId: String): OpenSourceVersionsGetResponse {
        val responseDto = openSourceVersionService.getVersions(openSourceId)
        return OpenSourceVersionsGetResponse(responseDto)
    }

    @GetMapping("/{openSourceVersionId}")
    fun getVersion(@PathVariable openSourceId: String, @PathVariable openSourceVersionId: String): OpenSourceVersionGetResponse {
        val responseDto = openSourceVersionService.getVersion(openSourceId, openSourceVersionId)
        return OpenSourceVersionGetResponse(responseDto)
    }
}
