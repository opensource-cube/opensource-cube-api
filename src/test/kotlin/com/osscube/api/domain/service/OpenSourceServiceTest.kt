package com.osscube.api.domain.service

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.dto.OpenSourceSaveRequestDto
import com.osscube.api.domain.exception.open_source.OpenSourceAlreadyExistsException
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.repository.OpenSourceRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class OpenSourceServiceTest : TestContainers() {
    @Autowired
    private lateinit var openSourceService: OpenSourceService

    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @AfterEach
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

    @DisplayName("모든 오픈소스를 조회한다.")
    @Test
    fun getOpenSources() {
        // given
        val given = listOf(
            OpenSource("name1", "origin url"),
            OpenSource("name2", "origin url"),
            OpenSource("name3", "origin url")
        )
        openSourceRepository.saveAll(given)

        // when
        val openSources = openSourceService.getOpenSources()

        // then
        openSources.forEach { assertThat(it.openSourceId).hasSize(36) }
        assertThat(openSources)
            .hasSize(3)
            .extracting("name", "originUrl")
            .contains(
                tuple("name1", "origin url"),
                tuple("name2", "origin url"),
                tuple("name3", "origin url")
            )
    }

    @DisplayName("오픈소스가 존재하지 않는 경우 빈 목록을 조회한다.")
    @Test
    fun getEmptyListIfOpenSourceNotExists() {
        // given

        // when
        val openSources = openSourceService.getOpenSources()

        // then
        assertThat(openSources)
            .hasSize(0)
    }
}
