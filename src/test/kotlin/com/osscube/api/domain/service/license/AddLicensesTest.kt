package com.osscube.api.domain.service.license

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.dto.LicenseAddRequestDto
import com.osscube.api.domain.exception.file.InvalidFileException
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.LicenseRepository
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
import com.osscube.api.domain.service.LicenseService
import com.osscube.api.utils.FileUtil
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile

@SpringBootTest
class AddLicensesTest : TestContainers() {
    @Autowired
    private lateinit var licenseService: LicenseService

    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @Autowired
    private lateinit var openSourceVersionRepository: OpenSourceVersionRepository

    @Autowired
    private lateinit var licenseRepository: LicenseRepository

    companion object {
        @JvmStatic
        @BeforeAll
        fun init() {
            mockkObject(FileUtil)
        }

        @JvmStatic
        @AfterAll
        fun destroy() {
            unmockkObject(FileUtil)
        }
    }

    @AfterEach
    fun cleansing() {
        licenseRepository.deleteAllInBatch()
        openSourceVersionRepository.deleteAllInBatch()
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("특정 버전의 오픈소스에 대한 라이선스 정보를 저장한다.")
    @Test
    fun addLicenses() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        val version = "20240303"
        val sourceUrl = "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz"
        val openSourceVersion = OpenSourceVersion(openSource, version, sourceUrl)
        openSourceVersionRepository.save(openSourceVersion)

        every { FileUtil.uploadFile(any(), any(), listOf(MediaType.TEXT_PLAIN)) } returns Unit

        // when
        val licenseDtos = listOf(
            LicenseAddRequestDto("MIT", MockMultipartFile("file", "license1.txt", MediaType.TEXT_PLAIN_VALUE, "MIT License".toByteArray())),
            LicenseAddRequestDto("APACHE2.0", MockMultipartFile("file", "license2.txt", MediaType.TEXT_PLAIN_VALUE, "APACHE2.0 License".toByteArray()))
        )
        val licenses = licenseService.addLicenses(openSourceVersion, licenseDtos)

        // then
        assertThat(licenseRepository.findAll()).hasSize(licenses.size)

        licenses.forEach { license ->
            assertThat(license.id).isNotNull()
            assertThat(license.clientId).hasSize(36)
            assertThat(license.openSourceVersion).isEqualTo(openSourceVersion)
        }

        assertThat(licenses)
            .hasSize(licenseDtos.size)
            .extracting("type", "path")
            .contains(
                tuple(licenseDtos[0].type, "/${openSource.name}_${openSource.id}/${openSourceVersion.version}/${licenseDtos[0].file.originalFilename}"),
                tuple(licenseDtos[1].type, "/${openSource.name}_${openSource.id}/${openSourceVersion.version}/${licenseDtos[1].file.originalFilename}")
            )
    }

    @DisplayName("라이선스 파일이 텍스트 파일이 아니면 라이선스 정보를 저장할 수 없다.")
    @Test
    fun cannotAddLicensesIfLicenseIsNotTextFile() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        val version = "20240303"
        val sourceUrl = "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz"
        val openSourceVersion = OpenSourceVersion(openSource, version, sourceUrl)
        openSourceVersionRepository.save(openSourceVersion)

        // when // then
        val licenseDtos = listOf(
            LicenseAddRequestDto("MIT", MockMultipartFile("file", "source.jar", MediaType.APPLICATION_OCTET_STREAM_VALUE, "source jar".toByteArray())),
            LicenseAddRequestDto("APACHE2.0", MockMultipartFile("file", "license2.txt", MediaType.TEXT_PLAIN_VALUE, "APACHE2.0 License".toByteArray()))
        )
        assertThatThrownBy { licenseService.addLicenses(openSourceVersion, licenseDtos) }
            .isInstanceOf(InvalidFileException::class.java)
    }
}
