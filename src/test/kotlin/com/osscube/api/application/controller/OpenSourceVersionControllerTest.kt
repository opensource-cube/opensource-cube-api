package com.osscube.api.application.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.osscube.api.config.TestContainers
import com.osscube.api.domain.dto.OpenSourceVersionAddRequestDto
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
import org.hamcrest.Matchers
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class OpenSourceVersionControllerTest : TestContainers() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @Autowired
    private lateinit var openSourceVersionRepository: OpenSourceVersionRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun cleansing() {
        openSourceVersionRepository.deleteAllInBatch()
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("오픈소스 id, 버전, 소스 코드 다운로드 경로를 입력받아 오픈소스에 버전을 추가한다.")
    @Test
    fun addNewVersion() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        // when // then
        val clientId = openSource.clientId
        val requestDto = OpenSourceVersionAddRequestDto("20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/open-sources/{openSourceId}/versions", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.openSourceVersion.id").value(Matchers.isA(String::class.java), String::class.java))
            .andExpect(jsonPath("$.openSourceVersion.version").value(requestDto.version))
            .andExpect(jsonPath("$.openSourceVersion.sourceUrl").value(requestDto.sourceUrl))
    }

    @DisplayName("오픈소스가 존재하지 않으면 버전을 추가할 수 없다.")
    @Test
    fun cannotAddNewVersionIfOpenSourceNotExists() {
        // given

        // when // then
        val clientId = "invalid client id"
        val requestDto = OpenSourceVersionAddRequestDto("20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/open-sources/{openSourceId}/versions", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
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
        val clientId = openSource.clientId
        val requestDto = OpenSourceVersionAddRequestDto("20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/open-sources/{openSourceId}/versions", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
            .andExpect(jsonPath("$.error").value("OPEN_SOURCE_VERSION-002"))
            .andExpect(jsonPath("$.message").value("이미 추가된 오픈소스 버전입니다."))
    }

    @DisplayName("openSourceId에 해당하는 모든 버전을 조회한다.")
    @Test
    fun getVersions() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        val givenVersions = listOf(
            OpenSourceVersion(openSource, "20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz"),
            OpenSourceVersion(openSource, "20240205", "https://github.com/stleary/JSON-java/archive/refs/tags/20240205.tar.gz"),
            OpenSourceVersion(openSource, "20231013", null)
        )
        openSourceVersionRepository.saveAll(givenVersions)

        // when // then
        val openSourceId = openSource.clientId
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/open-sources/{openSourceId}/versions", openSourceId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.openSourceVersions").isArray)
            .andExpect(jsonPath("$.openSourceVersions.length()").value(givenVersions.size))
            .andExpect(jsonPath("$.openSourceVersions[*].id").value(containsInAnyOrder(givenVersions[0].clientId, givenVersions[1].clientId, givenVersions[2].clientId)))
            .andExpect(jsonPath("$.openSourceVersions[*].version").value(containsInAnyOrder("20240303", "20240205", "20231013")))
            .andExpect(jsonPath("$.openSourceVersions[*].sourceUrl").value(containsInAnyOrder("https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz", "https://github.com/stleary/JSON-java/archive/refs/tags/20240205.tar.gz", null)))
    }

    @DisplayName("오픈소스가 존재하지 않으면 버전 목록을 조회할 수 없다.")
    @Test
    fun test() {
        // given

        // when // then
        val openSourceId = "invalid open source id"
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/open-sources/{openSourceId}/versions", openSourceId)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
            .andExpect(jsonPath("$.error").value("OPEN_SOURCE-001"))
            .andExpect(jsonPath("$.message").value("오픈소스를 찾을 수 없습니다."))
    }
}
