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
import org.junit.jupiter.api.BeforeEach
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

    private lateinit var openSource: OpenSource
    private lateinit var openSourceVersion: OpenSourceVersion

    companion object {
        @JvmStatic
        @BeforeAll
        fun staticInit() {
            mockkObject(FileUtil)
        }

        @JvmStatic
        @AfterAll
        fun staticDestroy() {
            unmockkObject(FileUtil)
        }
    }

    @BeforeEach
    fun init() {
        // given
        openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        openSourceVersion = OpenSourceVersion(openSource, "20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")
        openSourceVersionRepository.save(openSourceVersion)
    }

    @AfterEach
    fun cleansing() {
        licenseRepository.deleteAllInBatch()
        openSourceVersionRepository.deleteAllInBatch()
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("특정 버전의 오픈소스에 대한 라이선스 정보를 저장한다.")
    @Test
    fun `add licenses order by type`() {
        // given
        every { FileUtil.uploadFile(any(), any(), listOf(MediaType.TEXT_PLAIN)) } returns Unit

        // when
        val licenseDtos = listOf(
            LicenseAddRequestDto("MIT", MockMultipartFile("file", "license1.txt", MediaType.TEXT_PLAIN_VALUE, "MIT License".toByteArray())),
            LicenseAddRequestDto("APACHE2.0", MockMultipartFile("file", "license2.txt", MediaType.TEXT_PLAIN_VALUE, "APACHE2.0 License".toByteArray())),
            LicenseAddRequestDto("JSON License", MockMultipartFile("file", "license3.txt", MediaType.TEXT_PLAIN_VALUE, "JSON License".toByteArray()))
        )
        val licenses = licenseService.addLicenses(openSourceVersion, licenseDtos)

        // then
        assertThat(licenseRepository.findAll()).hasSize(licenses.size)

        licenses.forEach { license ->
            assertThat(license.id).isNotNull()
            assertThat(license.clientId).hasSize(36)
            assertThat(license.openSourceVersion).isEqualTo(openSourceVersion)
        }

        val sortedLicenseDtos = licenseDtos.sortedBy { it.type }
        assertThat(licenses)
            .hasSize(licenseDtos.size)
            .extracting("type", "path")
            .containsExactly(
                tuple(sortedLicenseDtos[0].type, "/${openSource.name}_${openSource.id}/${openSourceVersion.version}/${sortedLicenseDtos[0].file.originalFilename}"),
                tuple(sortedLicenseDtos[1].type, "/${openSource.name}_${openSource.id}/${openSourceVersion.version}/${sortedLicenseDtos[1].file.originalFilename}"),
                tuple(sortedLicenseDtos[2].type, "/${openSource.name}_${openSource.id}/${openSourceVersion.version}/${sortedLicenseDtos[2].file.originalFilename}")
            )
    }

    @DisplayName("라이선스 파일이 텍스트 파일이 아니면 라이선스 정보를 저장할 수 없다.")
    @Test
    fun `cannot add licenses if any license is not text file`() {
        // when // then
        val licenseDtos = listOf(
            LicenseAddRequestDto("MIT", MockMultipartFile("file", "source.jar", MediaType.APPLICATION_OCTET_STREAM_VALUE, "source jar".toByteArray()))
        )
        assertThatThrownBy { licenseService.addLicenses(openSourceVersion, licenseDtos) }
            .isInstanceOf(InvalidFileException::class.java)
    }
}
