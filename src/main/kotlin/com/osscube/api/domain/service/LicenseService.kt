package com.osscube.api.domain.service

import com.osscube.api.domain.dto.LicenseAddRequestDto
import com.osscube.api.domain.exception.file.InvalidFileException
import com.osscube.api.domain.exception.upper.FileException
import com.osscube.api.domain.model.entity.License
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.LicenseRepository
import com.osscube.api.utils.FileUtil
import java.io.File
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LicenseService(
    private val licenseRepository: LicenseRepository,

    @Value("\${application.storage}")
    private val storage: String
) {
    @Transactional
    fun addLicenses(openSourceVersion: OpenSourceVersion, licenseDtos: List<LicenseAddRequestDto>): List<License> {
        try {
            val licenses = licenseDtos.map { licenseDto ->
                val path = "/${openSourceVersion.openSource.name}_${openSourceVersion.openSource.id}/${openSourceVersion.version}/${licenseDto.file.originalFilename!!}"
                val dst = File(storage, path)
                FileUtil.uploadFile(licenseDto.file, dst, listOf(MediaType.TEXT_PLAIN))
                License(openSourceVersion, licenseDto.type, path)
            }
            licenseRepository.saveAll(licenses)
            openSourceVersion.licenses.addAll(licenses)
            return licenses
        } catch (e: FileException) {
            val path = "/${openSourceVersion.openSource.name}_${openSourceVersion.openSource.id}"
            val root = File(storage, path)
            root.deleteRecursively()
            throw InvalidFileException()
        }
    }
}
