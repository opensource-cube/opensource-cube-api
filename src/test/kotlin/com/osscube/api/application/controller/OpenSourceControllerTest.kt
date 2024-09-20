package com.osscube.api.application.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.osscube.api.application.request.OpenSourceSaveRequest
import com.osscube.api.config.TestContainers
import com.osscube.api.domain.dto.OpenSourceSaveRequestDto
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.repository.OpenSourceRepository
import org.hamcrest.Matchers
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
class OpenSourceControllerTest : TestContainers() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun cleansing() {
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("오픈소스명, 오픈소스 출처를 입력받아 오픈소스를 저장한다.")
    @Test
    fun saveOpenSource() {
        // given
        val request = OpenSourceSaveRequest("name", "origin url")

        // when // then
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post("/api/v1/open-sources")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect { status().isCreated }
            .andExpect(jsonPath("$.openSource.openSourceId").value(Matchers.isA(String::class.java), String::class.java))
            .andExpect(jsonPath("$.openSource.name").value("name"))
            .andExpect(jsonPath("$.openSource.originUrl").value("origin url"))
    }

    @DisplayName("이미 저장된 오픈소스를 저장하려하면 예외가 발생한다.")
    @Test
    fun saveOpenSourceFailIfOpenSourceExists() {
        // given
        val request = OpenSourceSaveRequest("name", "origin url")
        val openSource = OpenSource.of(OpenSourceSaveRequestDto.of(request))
        openSourceRepository.save(openSource)

        // when // then
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post("/api/v1/open-sources")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
            .andExpect(jsonPath("$.error").value("OPEN_SOURCE-001"))
            .andExpect(jsonPath("$.message").value("이미 저장된 오픈소스입니다."))
    }
}
