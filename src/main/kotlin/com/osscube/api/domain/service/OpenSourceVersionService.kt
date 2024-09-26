package com.osscube.api.domain.service

import com.osscube.api.domain.dto.OpenSourceVersionAddRequestDto
import com.osscube.api.domain.dto.OpenSourceVersionAddResponseDto
import com.osscube.api.domain.dto.OpenSourceVersionGetResponseDto
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
    fun addNewVersion(clientId: String, requestDto: OpenSourceVersionAddRequestDto): OpenSourceVersionAddResponseDto {
        val openSource = openSourceRepository.findByClientId(clientId) ?: throw OpenSourceNotFoundException()
        if (openSourceVersionRepository.existsByOpenSourceAndVersion(openSource, requestDto.version)) {
            throw OpenSourceVersionAlreadyExistsException()
        }

        val openSourceVersion = OpenSourceVersion(openSource, requestDto.version, requestDto.sourceUrl)
        openSourceVersionRepository.save(openSourceVersion)

        return OpenSourceVersionAddResponseDto.of(openSourceVersion)
    }

    fun getVersions(clientId: String): List<OpenSourceVersionGetResponseDto> {
        val openSource = openSourceRepository.findByClientId(clientId) ?: throw OpenSourceNotFoundException()
        return openSourceVersionRepository.findAllByOpenSource(openSource)
            .map { OpenSourceVersionGetResponseDto.of(it) }
    }
}
