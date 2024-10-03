package com.osscube.api.application.controller.open_source_version

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.dto.LicenseAddRequestDto
import com.osscube.api.domain.dto.OpenSourceVersionAddRequestDto
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.LicenseRepository
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
import com.osscube.api.utils.FileUtil
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AddNewVersionTest : TestContainers() {
    @Autowired
    private lateinit var mockMvc: MockMvc

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

    @DisplayName("오픈소스 id, 버전, 소스 코드 다운로드 경로, 라이선스 정보를 입력받아 오픈소스에 버전을 추가한다.")
    @Test
    fun addNewVersion() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        every { FileUtil.uploadFile(any(), any(), listOf(MediaType.TEXT_PLAIN)) } returns Unit

        // when // then
        val openSourceId = openSource.clientId
        val openSourceVersionDto = OpenSourceVersionAddRequestDto("20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")
        val licenseDtos = listOf(
            LicenseAddRequestDto("MIT", MockMultipartFile("licenses[0].file", "license1.txt", MediaType.TEXT_PLAIN_VALUE, "MIT License".toByteArray())),
            LicenseAddRequestDto("APACHE2.0", MockMultipartFile("licenses[1].file", "license2.txt", MediaType.TEXT_PLAIN_VALUE, "APACHE2.0 License".toByteArray()))
        )

        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart(HttpMethod.POST, "/api/v1/open-sources/{openSourceId}/versions", openSourceId)
                .file(licenseDtos[0].file as MockMultipartFile)
                .file(licenseDtos[1].file as MockMultipartFile)
                .param("version", openSourceVersionDto.version)
                .param("sourceUrl", openSourceVersionDto.sourceUrl)
                .param("licenses[0].type", "MIT")
                .param("licenses[1].type", "APACHE2.0")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.openSourceVersion.id").value(Matchers.isA(String::class.java), String::class.java))
            .andExpect(jsonPath("$.openSourceVersion.version").value(openSourceVersionDto.version))
            .andExpect(jsonPath("$.openSourceVersion.sourceUrl").value(openSourceVersionDto.sourceUrl))
            .andExpect(jsonPath("$.openSourceVersion.licenses.length()").value(licenseDtos.size))
            .andExpect(jsonPath("$.openSourceVersion.licenses[*].type").value(Matchers.containsInAnyOrder(licenseDtos[0].type, licenseDtos[1].type)))
    }

    @DisplayName("오픈소스가 존재하지 않으면 버전을 추가할 수 없다.")
    @Test
    fun cannotAddNewVersionIfOpenSourceNotExists() {
        // given

        // when // then
        val invalidOpenSourceId = "invalid client id"
        val openSourceVersionDto = OpenSourceVersionAddRequestDto("20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")

        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart(HttpMethod.POST, "/api/v1/open-sources/{openSourceId}/versions", invalidOpenSourceId)
                .param("version", openSourceVersionDto.version)
                .param("sourceUrl", openSourceVersionDto.sourceUrl)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
            .andExpect(jsonPath("$.error").value("OPEN_SOURCE-001"))
            .andExpect(jsonPath("$.message").value("오픈소스를 찾을 수 없습니다."))
    }

    @DisplayName("이미 추가한 버전을 다시 추가할 수 없다.")
    @Test
    fun cannotAddNewVersionIfVersionAlreadyAdded() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        val openSourceVersion = OpenSourceVersion(openSource, "20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")
        openSourceVersionRepository.save(openSourceVersion)

        // when // then
        val openSourceId = openSource.clientId
        val openSourceVersionDto = OpenSourceVersionAddRequestDto("20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")

        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart(HttpMethod.POST, "/api/v1/open-sources/{openSourceId}/versions", openSourceId)
                .param("version", openSourceVersionDto.version)
                .param("sourceUrl", openSourceVersionDto.sourceUrl)
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
            .andExpect(jsonPath("$.error").value("OPEN_SOURCE_VERSION-002"))
            .andExpect(jsonPath("$.message").value("이미 추가된 오픈소스 버전입니다."))
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
            LicenseAddRequestDto("MIT", MockMultipartFile("licenses[0].file", "source.jar", MediaType.TEXT_PLAIN_VALUE, "source".toByteArray())),
            LicenseAddRequestDto("APACHE2.0", MockMultipartFile("licenses[1].file", "license2.txt", MediaType.TEXT_PLAIN_VALUE, "APACHE2.0 License".toByteArray()))
        )

        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart(HttpMethod.POST, "/api/v1/open-sources/{openSourceId}/versions", openSourceId)
                .file(licenseDtos[0].file as MockMultipartFile)
                .file(licenseDtos[1].file as MockMultipartFile)
                .param("version", openSourceVersionDto.version)
                .param("sourceUrl", openSourceVersionDto.sourceUrl)
                .param("licenses[0].type", "MIT")
                .param("licenses[1].type", "APACHE2.0")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
            .andExpect(jsonPath("$.error").value("FILE-001"))
            .andExpect(jsonPath("$.message").value("유효하지 않은 파일입니다."))
    }
}
