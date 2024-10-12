package com.osscube.api.application.controller

import com.osscube.api.application.request.OpenSourceSaveRequest
import com.osscube.api.application.request.OpenSourceUpdateRequest
import com.osscube.api.application.response.OpenSourceGetResponse
import com.osscube.api.application.response.OpenSourceSaveResponse
import com.osscube.api.application.response.OpenSourceUpdateResponse
import com.osscube.api.domain.dto.OpenSourceSaveRequestDto
import com.osscube.api.domain.dto.OpenSourceUpdateRequestDto
import com.osscube.api.domain.service.OpenSourceService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getOpenSources(): OpenSourceGetResponse {
        val responseDto = openSourceService.getOpenSources()
        return OpenSourceGetResponse(responseDto)
    }

    @PutMapping("/{openSourceId}")
    @ResponseStatus(HttpStatus.OK)
    fun updateOpenSource(@PathVariable openSourceId: String, @RequestBody request: OpenSourceUpdateRequest): OpenSourceUpdateResponse {
        val responseDto = openSourceService.updateOpenSource(openSourceId, OpenSourceUpdateRequestDto.of(request))
        return OpenSourceUpdateResponse.of(responseDto)
    }

    @DeleteMapping("/{openSourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteOpenSource(@PathVariable openSourceId: String) {
        openSourceService.deleteOpenSource(openSourceId)
    }
}
