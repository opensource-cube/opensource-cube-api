package com.osscube.api.application.controller.open_source_version

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.model.entity.License
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.LicenseRepository
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class GetVersionTest : TestContainers() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @Autowired
    private lateinit var openSourceVersionRepository: OpenSourceVersionRepository

    @Autowired
    private lateinit var licenseRepository: LicenseRepository

    @AfterEach
    fun cleansing() {
        licenseRepository.deleteAllInBatch()
        openSourceVersionRepository.deleteAllInBatch()
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("오픈소스 id와 오픈소스 버전 id로 특정 버전의 오픈소스를 조회한다.")
    @Test
    fun getVersion() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        val openSourceVersion = OpenSourceVersion(openSource, "20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")
        openSourceVersionRepository.save(openSourceVersion)

        val licenses = listOf(
            License(openSourceVersion, "APACHE2.0", "temp"),
            License(openSourceVersion, "MIT", "temp")
        ).sortedBy { it.type }
        licenseRepository.saveAll(licenses)
        openSourceVersion.licenses.addAll(licenses)

        // when // then
        val openSourceId = openSource.clientId
        val openSourceVersionId = openSourceVersion.clientId
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/open-sources/{openSourceId}/versions/{openSourceVersionId}", openSourceId, openSourceVersionId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.openSourceVersion.id").value(openSourceVersion.clientId))
            .andExpect(jsonPath("$.openSourceVersion.version").value(openSourceVersion.version))
            .andExpect(jsonPath("$.openSourceVersion.sourceUrl").value(openSourceVersion.sourceUrl))
            .andExpect(jsonPath("$.openSourceVersion.licenses[0].id").value(Matchers.isA(String::class.java), String::class.java))
            .andExpect(jsonPath("$.openSourceVersion.licenses[0].type").value(licenses[0].type))
            .andExpect(jsonPath("$.openSourceVersion.licenses[1].id").value(Matchers.isA(String::class.java), String::class.java))
            .andExpect(jsonPath("$.openSourceVersion.licenses[1].type").value(licenses[1].type))
    }

    @DisplayName("오픈소스 id에 해당하는 오픈소스가 존재하지 않으면 특정 버전의 오픈소스를 조회할 수 없다.")
    @Test
    fun cannotGetVersionIfOpenSourceIdIsInvalid() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        val openSourceVersion = OpenSourceVersion(openSource, "20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")
        openSourceVersionRepository.save(openSourceVersion)

        val licenses = listOf(
            License(openSourceVersion, "APACHE2.0", "temp"),
            License(openSourceVersion, "MIT", "temp")
        ).sortedBy { it.type }
        licenseRepository.saveAll(licenses)
        openSourceVersion.licenses.addAll(licenses)

        // when // then
        val invalidOpenSourceId = "invalid open source id"
        val openSourceVersionId = openSourceVersion.clientId
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/open-sources/{openSourceId}/versions/{openSourceVersionId}", invalidOpenSourceId, openSourceVersionId)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
            .andExpect(jsonPath("$.error").value("OPEN_SOURCE-001"))
            .andExpect(jsonPath("$.message").value("오픈소스를 찾을 수 없습니다."))
    }

    @DisplayName("오픈소스에 오픈소스 버전 id에 해당하는 오픈소스가 존재하지 않으면 특정 버전의 오픈소스를 조회할 수 없다.")
    @Test
    fun cannotGetVersionIfOpenSourceVersionIdIsInvalid() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        val openSourceVersion = OpenSourceVersion(openSource, "20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")
        openSourceVersionRepository.save(openSourceVersion)

        val licenses = listOf(
            License(openSourceVersion, "APACHE2.0", "temp"),
            License(openSourceVersion, "MIT", "temp")
        ).sortedBy { it.type }
        licenseRepository.saveAll(licenses)
        openSourceVersion.licenses.addAll(licenses)

        // when // then
        val openSourceId = openSource.clientId
        val invalidOpenSourceVersionId = "invalidOpenSourceVersionId"
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/open-sources/{openSourceId}/versions/{openSourceVersionId}", openSourceId, invalidOpenSourceVersionId)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
            .andExpect(jsonPath("$.error").value("OPEN_SOURCE_VERSION-001"))
            .andExpect(jsonPath("$.message").value("특정 버전의 오픈소스가 존재하지 않습니다."))
    }
}
