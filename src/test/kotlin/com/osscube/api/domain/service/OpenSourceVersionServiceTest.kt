package com.osscube.api.domain.service

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.dto.OpenSourceVersionAddRequestDto
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.repository.OpenSourceRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class OpenSourceVersionServiceTest : TestContainers() {
    @Autowired
    private lateinit var openSourceVersionService: OpenSourceVersionService

    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @DisplayName("오픈소스에 새로운 버전을 추가한다.")
    @Test
    fun addNewVersion() {
        // given
        val openSource = OpenSource("name", "origin url")
        openSourceRepository.save(openSource)

        // when
        val requestDto = OpenSourceVersionAddRequestDto(openSource.clientId, "v1.0.0", null)
        val responseDto = openSourceVersionService.addNewVersion(requestDto)

        // then
        assertThat(responseDto.id)
            .hasSize(36)
        assertThat(responseDto.version)
            .isEqualTo("v1.0.0")
    }
}
