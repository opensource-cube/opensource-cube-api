package com.osscube.api.domain.service

import com.osscube.api.domain.dto.OpenSourceSaveRequestDto
import com.osscube.api.domain.dto.OpenSourceSaveResponseDto
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.repository.OpenSourceRepository
import org.springframework.stereotype.Service

@Service
class OpenSourceService(
    private val openSourceRepository: OpenSourceRepository
) {
    fun saveOpenSource(dto: OpenSourceSaveRequestDto): OpenSourceSaveResponseDto {
        val openSource = OpenSource.of(dto)
        openSourceRepository.save(openSource)
        return OpenSourceSaveResponseDto.of(openSource)
    }
}
