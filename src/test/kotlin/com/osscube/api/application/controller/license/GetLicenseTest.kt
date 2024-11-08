package com.osscube.api.application.controller.license

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.model.entity.License
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.LicenseRepository
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
import com.osscube.api.utils.FileUtil
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import java.io.File
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class GetLicenseTest : TestContainers() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @Autowired
    private lateinit var openSourceVersionRepository: OpenSourceVersionRepository

    @Autowired
    private lateinit var licenseRepository: LicenseRepository

    private lateinit var openSources: List<OpenSource>
    private lateinit var openSourceVersions: List<OpenSourceVersion>
    private lateinit var license: License

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
        openSources = listOf(
            OpenSource("JSON-java", "https://github.com/stleary/JSON-java"),
            OpenSource("openssl", "https://github.com/openssl/openssl")
        )
        openSourceRepository.saveAll(openSources)

        openSourceVersions = listOf(
            OpenSourceVersion(openSources[0], "20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz"),
            OpenSourceVersion(openSources[0], "20240205", "https://github.com/stleary/JSON-java/archive/refs/tags/20240205.tar.gz"),
            OpenSourceVersion(openSources[1], "openssl-3.4.0", "https://github.com/openssl/openssl/archive/refs/tags/openssl-3.4.0.tar.gz"),
            OpenSourceVersion(openSources[1], "openssl-3.3.2", "https://github.com/openssl/openssl/archive/refs/tags/openssl-3.3.2.tar.gz"),
        )
        openSourceVersionRepository.saveAll(openSourceVersions)

        license = License(openSourceVersions[0], "Public Domain.", "temp")
        licenseRepository.save(license)
        openSourceVersions[0].licenses.add(license)
    }

    @AfterEach
    fun cleansing() {
        licenseRepository.deleteAllInBatch()
        openSourceVersionRepository.deleteAllInBatch()
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("라이선스를 조회한다.")
    @Test
    fun `get license`() {
        // given
        every { FileUtil.readText(any(File::class)) } returns "Content of JSON License"

        // when // then
        val openSourceId = openSources[0].clientId
        val openSourceVersionId = openSourceVersions[0].clientId
        val licenseId = license.clientId

        mockMvc.perform(
            get("/api/v1/open-sources/{openSourceId}/versions/{openSourceVersionId}/licenses/{licenseId}", openSourceId, openSourceVersionId, licenseId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.license.id").value(license.clientId))
            .andExpect(jsonPath("$.license.type").value(license.type))
            .andExpect(jsonPath("$.license.content").value("Content of JSON License"))
    }

    @DisplayName("오픈소스가 존재하지 않으면 라이선스를 조회할 수 없다.")
    @Test
    fun `cannot get license if open source is not exist`() {
        // when // then
        val invalidOpenSourceId = "invalid open source id"
        val openSourceVersionId = openSourceVersions[0].clientId
        val licenseId = license.clientId

        mockMvc.perform(
            get("/api/v1/open-sources/{openSourceId}/versions/{openSourceVersionId}/licenses/{licenseId}", invalidOpenSourceId, openSourceVersionId, licenseId)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
            .andExpect(jsonPath("$.error").value("OPEN_SOURCE-001"))
            .andExpect(jsonPath("$.message").value("오픈소스를 찾을 수 없습니다."))
    }

    @DisplayName("오픈소스에 해당 버전이 존재하지 않으면 라이선스를 조회할 수 없다.")
    @Test
    fun `cannot get license if open source version is not exist`() {
        // when // then
        val openSourceId = openSources[0].clientId
        val openSourceVersionId = openSourceVersions[2].clientId
        val licenseId = license.clientId

        mockMvc.perform(
            get("/api/v1/open-sources/{openSourceId}/versions/{openSourceVersionId}/licenses/{licenseId}", openSourceId, openSourceVersionId, licenseId)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
            .andExpect(jsonPath("$.error").value("OPEN_SOURCE_VERSION-001"))
            .andExpect(jsonPath("$.message").value("특정 버전의 오픈소스가 존재하지 않습니다."))
    }

    @DisplayName("오픈소스 버전에 존재하지 않는 라이선스를 조회할 수 없다.")
    @Test
    fun `cannot get license if license is not exist`() {
        // when // then
        val openSourceId = openSources[0].clientId
        val openSourceVersionId = openSourceVersions[0].clientId
        val invalidLicenseId = "invalid license id"

        mockMvc.perform(
            get("/api/v1/open-sources/{openSourceId}/versions/{openSourceVersionId}/licenses/{licenseId}", openSourceId, openSourceVersionId, invalidLicenseId)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
            .andExpect(jsonPath("$.error").value("LICENSE-001"))
            .andExpect(jsonPath("$.message").value("라이선스가 존재하지 않습니다."))
    }
}
