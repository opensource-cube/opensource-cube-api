package com.osscube.api.domain.service.open_source_version

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.dto.LicenseAddRequestDto
import com.osscube.api.domain.dto.OpenSourceVersionAddRequestDto
import com.osscube.api.domain.exception.file.InvalidFileException
import com.osscube.api.domain.exception.open_source.OpenSourceNotFoundException
import com.osscube.api.domain.exception.open_source_version.OpenSourceVersionAlreadyExistsException
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.LicenseRepository
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
import com.osscube.api.domain.service.OpenSourceVersionService
import com.osscube.api.utils.FileUtil
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class AddNewVersionTest : TestContainers() {
    @Autowired
    private lateinit var openSourceVersionService: OpenSourceVersionService

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

    @BeforeEach
    fun cleansing() {
        licenseRepository.deleteAllInBatch()
        openSourceVersionRepository.deleteAllInBatch()
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("오픈소스에 새로운 버전을 추가한다.")
    @Test
    fun addNewVersion() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        every { FileUtil.uploadFile(any(), any(), listOf(MediaType.TEXT_PLAIN)) } returns Unit

        // when
        val openSourceId = openSource.clientId
        val openSourceVersionDto = OpenSourceVersionAddRequestDto("20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")
        val licenseDtos = listOf(
            LicenseAddRequestDto("MIT", MockMultipartFile("file", "license1.txt", MediaType.TEXT_PLAIN_VALUE, "MIT License".toByteArray())),
            LicenseAddRequestDto("APACHE2.0", MockMultipartFile("file", "license2.txt", MediaType.TEXT_PLAIN_VALUE, "APACHE2.0 License".toByteArray()))
        )
        val responseDto = openSourceVersionService.addNewVersion(openSourceId, openSourceVersionDto, licenseDtos)

        // then
        val openSourceVersion = openSourceVersionRepository.findByOpenSourceAndClientId(openSource, responseDto.id)!!
        assertThat(openSourceVersion.licenses)
            .hasSize(licenseDtos.size)

        val licenses = licenseRepository.findAll()
        assertThat(licenses)
            .hasSize(licenseDtos.size)
            .extracting("openSourceVersion.id")
            .contains(openSourceVersion.id)

        assertThat(responseDto.id)
            .hasSize(36)
        assertThat(responseDto)
            .extracting("version", "sourceUrl")
            .contains(openSourceVersionDto.version, openSourceVersionDto.sourceUrl)

        responseDto.licenses.forEach { license ->
            assertThat(license.id)
                .hasSize(36)
        }
        assertThat(responseDto.licenses)
            .hasSize(2)
            .extracting("type")
            .contains(licenseDtos[0].type, licenseDtos[1].type)
    }

    @DisplayName("오픈소스가 없으면 새로운 버전을 추가할 수 없다.")
    @Test
    fun cannotAddNewVersionIfOpenSourceNotExist() {
        // given

        // when // then
        val invalidOpenSourceId = "invalid open source id"
        val openSourceVersionDto = OpenSourceVersionAddRequestDto("v1.0.0", null)
        val licenseDtos = listOf(
            LicenseAddRequestDto("JSON License", MockMultipartFile("file", "license.txt", MediaType.TEXT_PLAIN_VALUE, "JSON License".toByteArray())),
        )
        assertThatThrownBy { openSourceVersionService.addNewVersion(invalidOpenSourceId, openSourceVersionDto, licenseDtos) }
            .isInstanceOf(OpenSourceNotFoundException::class.java)
    }

    @DisplayName("오픈소스 이미 추가된 버전을 다시 추가할 수 없다.")
    @Test
    fun cannotAddNewVersionIfAlreadyAdded() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        val version = "20240303"
        val sourceUrl = "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz"
        val openSourceVersion = OpenSourceVersion(openSource, version, sourceUrl)
        openSourceVersionRepository.save(openSourceVersion)

        // when // then
        val openSourceId = openSource.clientId
        val openSourceVersionDto = OpenSourceVersionAddRequestDto(version, sourceUrl)
        val licenseDtos = listOf(
            LicenseAddRequestDto("JSON License", MockMultipartFile("file", "license.txt", MediaType.TEXT_PLAIN_VALUE, "JSON License".toByteArray())),
        )
        assertThatThrownBy { openSourceVersionService.addNewVersion(openSourceId, openSourceVersionDto, licenseDtos) }
            .isInstanceOf(OpenSourceVersionAlreadyExistsException::class.java)
    }

    @DisplayName("라이선스 파일이 text 파일이 아니면 새로운 버전을 추가할 수 없다.")
    @Test
    fun cannotAddNewVersionIfLicenseFileIsNotTextFile() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        // when // then
        val openSourceId = openSource.clientId
        val openSourceVersionDto = OpenSourceVersionAddRequestDto("20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")
        val licenseDtos = listOf(
            LicenseAddRequestDto("MIT", MockMultipartFile("file", "source.jar", MediaType.APPLICATION_OCTET_STREAM_VALUE, "source jar".toByteArray()))
        )
        assertThatThrownBy { openSourceVersionService.addNewVersion(openSourceId, openSourceVersionDto, licenseDtos) }
            .isInstanceOf(InvalidFileException::class.java)
    }
}
