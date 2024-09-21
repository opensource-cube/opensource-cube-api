package com.osscube.api.domain.service

import com.osscube.api.domain.dto.OpenSourceVersionAddRequestDto
import com.osscube.api.domain.dto.OpenSourceVersionAddResponseDto
import com.osscube.api.domain.exception.open_source.OpenSourceNotFoundException
import com.osscube.api.domain.exception.open_source_version.OpenSourceVersionAlreadyExistsException
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
import org.springframework.stereotype.Service

@Service
class OpenSourceVersionService(
    private val openSourceRepository: OpenSourceRepository,
    private val openSourceVersionRepository: OpenSourceVersionRepository
) {
    fun addNewVersion(requestDto: OpenSourceVersionAddRequestDto): OpenSourceVersionAddResponseDto {
        val openSource = openSourceRepository.findByClientId(requestDto.openSourceId) ?: throw OpenSourceNotFoundException()
        if (openSourceVersionRepository.existsByOpenSourceAndVersion(openSource, requestDto.version)) {
            throw OpenSourceVersionAlreadyExistsException()
        }

        val openSourceVersion = OpenSourceVersion(openSource, requestDto.version, null)
        openSourceVersionRepository.save(openSourceVersion)

        return OpenSourceVersionAddResponseDto.of(openSourceVersion)
    }
}
