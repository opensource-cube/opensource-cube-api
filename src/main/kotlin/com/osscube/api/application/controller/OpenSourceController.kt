package com.osscube.api.application.controller

import com.osscube.api.application.request.OpenSourceSaveRequest
import com.osscube.api.application.response.OpenSourceSaveResponse
import com.osscube.api.domain.dto.OpenSourceSaveRequestDto
import com.osscube.api.domain.service.OpenSourceService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/open-sources")
class OpenSourceController(
    private val openSourceService: OpenSourceService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun saveOpenSource(@RequestBody request: OpenSourceSaveRequest): OpenSourceSaveResponse {
        val responseDto = openSourceService.saveOpenSource(OpenSourceSaveRequestDto.of(request))
        return OpenSourceSaveResponse(responseDto)
    }
}
