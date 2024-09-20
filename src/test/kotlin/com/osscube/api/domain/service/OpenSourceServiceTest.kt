package com.osscube.api.domain.service

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.dto.OpenSourceSaveRequestDto
import com.osscube.api.domain.exception.open_source.OpenSourceAlreadyExistsException
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.repository.OpenSourceRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class OpenSourceServiceTest : TestContainers() {
    @Autowired
    private lateinit var openSourceService: OpenSourceService

    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @BeforeEach
    fun cleansing() {
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("오픈소스명, 오픈소스 출처를 받아서 오픈소스를 저장한다.")
    @Test
    fun saveOpenSource() {
        // given
        val name = "name"
        val originUrl = "originUrl"
        val requestDto = OpenSourceSaveRequestDto(name, originUrl)

        // when
        val responseDto = openSourceService.saveOpenSource(requestDto)

        // then
        assertThat(responseDto.openSourceId)
            .hasSize(36)
        assertThat(responseDto)
            .extracting("name", "originUrl")
            .contains(name, originUrl)
    }

    @DisplayName("이미 저장된 오픈소스를 다시 저장하면 예외가 발생한다.")
    @Test
    fun saveOpenSourceThrowsExceptionIfOpenSourceExists() {
        // given
        val requestDto = OpenSourceSaveRequestDto("name", "originUrl")

        val openSource = OpenSource.of(requestDto)
        openSourceRepository.save(openSource)

        // when // then
        assertThatThrownBy { openSourceService.saveOpenSource(requestDto) }
            .isInstanceOf(OpenSourceAlreadyExistsException::class.java)
    }
}
