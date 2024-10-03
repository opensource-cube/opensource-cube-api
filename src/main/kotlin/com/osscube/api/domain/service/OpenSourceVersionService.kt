package com.osscube.api.domain.service

import com.osscube.api.domain.dto.LicenseAddRequestDto
import com.osscube.api.domain.dto.OpenSourceVersionAddRequestDto
import com.osscube.api.domain.dto.OpenSourceVersionAddNewVersionResponseDto
import com.osscube.api.domain.dto.OpenSourceVersionGetResponseDto
import com.osscube.api.domain.exception.open_source.OpenSourceNotFoundException
import com.osscube.api.domain.exception.open_source_version.OpenSourceVersionAlreadyExistsException
import com.osscube.api.domain.exception.open_source_version.OpenSourceVersionNotFoundException
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OpenSourceVersionService(
    private val openSourceRepository: OpenSourceRepository,
    private val openSourceVersionRepository: OpenSourceVersionRepository,
    private val licenseService: LicenseService
) {
    @Transactional
    fun addNewVersion(openSourceId: String, openSourceVersionDto: OpenSourceVersionAddRequestDto, licenseDtos: List<LicenseAddRequestDto>): OpenSourceVersionAddNewVersionResponseDto {
        val openSource = openSourceRepository.findByClientId(openSourceId) ?: throw OpenSourceNotFoundException()
        if (openSourceVersionRepository.existsByOpenSourceAndVersion(openSource, openSourceVersionDto.version)) {
            throw OpenSourceVersionAlreadyExistsException()
        }

        val openSourceVersion = OpenSourceVersion(openSource, openSourceVersionDto.version, openSourceVersionDto.sourceUrl)
        openSourceVersionRepository.save(openSourceVersion)

        val licenses = licenseService.addLicenses(openSourceVersion, licenseDtos)

        return OpenSourceVersionAddNewVersionResponseDto.of(openSourceVersion, licenses)
    }

    fun getVersions(clientId: String): List<OpenSourceVersionGetResponseDto> {
        val openSource = openSourceRepository.findByClientId(clientId) ?: throw OpenSourceNotFoundException()
        return openSourceVersionRepository.findAllByOpenSource(openSource)
            .map { OpenSourceVersionGetResponseDto.of(it) }
    }

    fun getVersion(openSourceId: String, openSourceVersionId: String): OpenSourceVersionGetResponseDto {
        val openSource = openSourceRepository.findByClientId(openSourceId) ?: throw OpenSourceNotFoundException()
        val openSourceVersion = openSourceVersionRepository.findByOpenSourceAndClientId(openSource, openSourceVersionId) ?: throw OpenSourceVersionNotFoundException()
        return OpenSourceVersionGetResponseDto.of(openSourceVersion)
    }
}
