package com.osscube.api.domain.service

import com.osscube.api.domain.dto.OpenSourceGetDto
import com.osscube.api.domain.dto.OpenSourceSaveRequestDto
import com.osscube.api.domain.dto.OpenSourceSaveResponseDto
import com.osscube.api.domain.dto.OpenSourceUpdateRequestDto
import com.osscube.api.domain.dto.OpenSourceUpdateResponseDto
import com.osscube.api.domain.exception.open_source.OpenSourceAlreadyExistsException
import com.osscube.api.domain.exception.open_source.OpenSourceNotFoundException
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.repository.OpenSourceRepository
import org.springframework.stereotype.Service

@Service
class OpenSourceService(
    private val openSourceRepository: OpenSourceRepository
) {
    fun saveOpenSource(dto: OpenSourceSaveRequestDto): OpenSourceSaveResponseDto {
        if (openSourceRepository.existsByNameAndOriginUrl(dto.name, dto.originUrl)) {
            throw OpenSourceAlreadyExistsException()
        }

        val openSource = OpenSource.of(dto)
        openSourceRepository.save(openSource)
        return OpenSourceSaveResponseDto.of(openSource)
    }

    fun getOpenSources(): List<OpenSourceGetDto> {
        return openSourceRepository.findAll()
            .map { OpenSourceGetDto.of(it) }
    }

    fun updateOpenSource(openSourceId: String, requestDto: OpenSourceUpdateRequestDto): OpenSourceUpdateResponseDto {
        val openSource = openSourceRepository.findByClientId(openSourceId) ?: throw OpenSourceNotFoundException()
        if (openSourceRepository.existsByNameAndOriginUrl(requestDto.name, requestDto.originUrl)) {
            throw OpenSourceAlreadyExistsException()
        }
        openSource.update(requestDto.name, requestDto.originUrl)
        return OpenSourceUpdateResponseDto.of(openSourceId, requestDto)
    }
}
