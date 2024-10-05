package com.osscube.api.application.controller.open_source_version

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@SpringBootTest
@AutoConfigureMockMvc
class GetVersionsTest : TestContainers() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @Autowired
    private lateinit var openSourceVersionRepository: OpenSourceVersionRepository

    @AfterEach
    fun cleansing() {
        openSourceVersionRepository.deleteAllInBatch()
        openSourceRepository.deleteAllInBatch()
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
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.openSourceVersions").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$.openSourceVersions.length()").value(givenVersions.size))
            .andExpect(MockMvcResultMatchers.jsonPath("$.openSourceVersions[*].id").value(Matchers.containsInAnyOrder(givenVersions[0].clientId, givenVersions[1].clientId, givenVersions[2].clientId)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.openSourceVersions[*].version").value(Matchers.containsInAnyOrder("20240303", "20240205", "20231013")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.openSourceVersions[*].sourceUrl").value(Matchers.containsInAnyOrder("https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz", "https://github.com/stleary/JSON-java/archive/refs/tags/20240205.tar.gz", null)))
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
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("OPEN_SOURCE-001"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("오픈소스를 찾을 수 없습니다."))
    }
}
