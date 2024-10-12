package com.osscube.api.application.controller.open_source

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.repository.OpenSourceRepository
import org.hamcrest.Matchers.containsInRelativeOrder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class GetOpenSourcesTest : TestContainers() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @AfterEach
    fun cleansing() {
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("오픈소스 목록을 조회한다.")
    @Test
    fun getOpenSources() {
        // given
        val given = listOf(
            OpenSource("name1", "origin url"),
            OpenSource("name2", "origin url"),
            OpenSource("name3", "origin url")
        )
        openSourceRepository.saveAll(given)

        // when // then
        mockMvc
            .perform(
                get("/api/v1/open-sources")
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.openSources").isArray)
            .andExpect(jsonPath("$.openSources.length()").value(given.size))
            .andExpect(jsonPath("$.openSources[*].name").value(containsInRelativeOrder("name1", "name2", "name3")))
            .andExpect(jsonPath("$.openSources[*].originUrl").value(containsInRelativeOrder("origin url", "origin url", "origin url")))
    }
}
