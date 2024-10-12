package com.osscube.api.application.controller.open_source

import com.fasterxml.jackson.databind.ObjectMapper
import com.osscube.api.application.request.OpenSourceUpdateRequest
import com.osscube.api.config.TestContainers
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.repository.OpenSourceRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class UpdateOpenSourceTest : TestContainers() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var openSource: OpenSource

    @BeforeEach
    fun init() {
        // given
        openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)
    }

    @AfterEach
    fun cleansing() {
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("오픈소스 정보를 갱신한다.")
    @Test
    fun `update open source`() {
        // given
        val openSourceId = openSource.clientId
        val request = OpenSourceUpdateRequest("json-java", "https://github.com/stleary/json-java")

        // when // then
        mockMvc.perform(
            put("/api/v1/open-sources/{openSourceId}", openSourceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect { status().isOk }
            .andExpect(jsonPath("$.openSource.id").value(openSourceId))
            .andExpect(jsonPath("$.openSource.name").value(request.name))
            .andExpect(jsonPath("$.openSource.originUrl").value(request.originUrl))
    }

    @DisplayName("오픈소스가 존재하지 않으면 오픈소스를 갱신할 수 없다.")
    @Test
    fun `cannot update open source if open source is not found`() {
        // given
        val invalidOpenSourceId = "invalid open source id"
        val request = OpenSourceUpdateRequest("json-java", "https://github.com/stleary/json-java")

        // when // then
        mockMvc.perform(
            put("/api/v1/open-sources/{openSourceId}", invalidOpenSourceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect { status().isNotFound }
            .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
            .andExpect(jsonPath("$.error").value("OPEN_SOURCE-001"))
            .andExpect(jsonPath("$.message").value("오픈소스를 찾을 수 없습니다."))
    }

    @DisplayName("갱신하려는 오픈소스가 이미 존재하면 오픈소스를 갱신할 수 없다.")
    @Test
    fun `cannot update open source if open source already exists`() {
        // given
        val openSourceId = openSource.clientId
        val request = OpenSourceUpdateRequest("JSON-java", "https://github.com/stleary/JSON-java")

        // when // then
        mockMvc.perform(
            put("/api/v1/open-sources/{openSourceId}", openSourceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect { status().isConflict }
            .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
            .andExpect(jsonPath("$.error").value("OPEN_SOURCE-002"))
            .andExpect(jsonPath("$.message").value("이미 저장된 오픈소스입니다."))
    }
}
