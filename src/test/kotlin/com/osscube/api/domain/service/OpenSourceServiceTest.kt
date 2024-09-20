package com.osscube.api.domain.service

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.dto.OpenSourceSaveRequestDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@ActiveProfiles
@Testcontainers
class OpenSourceServiceTest : TestContainers() {
    @Autowired
    private lateinit var openSourceService: OpenSourceService

    @DisplayName("오픈소스명, 오픈소스 출처를 받아서 오픈소스를 저장한다.")
    @Test
    fun saveOpenSource() {
        // given
        val name = "name"
        val originUrl = "originUrl"
        val requestDto = OpenSourceSaveRequestDto(name, originUrl)

        //when
        val responseDto = openSourceService.saveOpenSource(requestDto)

        //then
        assertThat(responseDto.clientId)
            .hasSize(36)
        assertThat(responseDto)
            .extracting("name", "originUrl")
            .contains(name, originUrl)
    }
}
