package com.osscube.api.application.controller.open_source_version

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
import org.junit.jupiter.api.BeforeEach
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
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class GetVersionTest : TestContainers() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @Autowired
    private lateinit var openSourceVersionRepository: OpenSourceVersionRepository

    @BeforeEach
    fun cleansing() {
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
    }

    @DisplayName("오픈소스 id에 해당하는 오픈소스가 존재하지 않으면 특정 버전의 오픈소스를 조회할 수 없다.")
    @Test
    fun cannotGetVersionIfOpenSourceIdIsInvalid() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        val openSourceVersion = OpenSourceVersion(openSource, "20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")
        openSourceVersionRepository.save(openSourceVersion)

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
