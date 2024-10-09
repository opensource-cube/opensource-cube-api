package com.osscube.api.domain.service.open_source

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.dto.OpenSourceUpdateRequestDto
import com.osscube.api.domain.exception.open_source.OpenSourceAlreadyExistsException
import com.osscube.api.domain.exception.open_source.OpenSourceNotFoundException
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.service.OpenSourceService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UpdateOpenSourceTest : TestContainers() {
    @Autowired
    private lateinit var openSourceService: OpenSourceService

    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

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

    @DisplayName("오픈소스명과 출처를 갱신한다.")
    @Test
    fun `update open source`() {
        // when
        val openSourceId = openSource.clientId
        val requestDto = OpenSourceUpdateRequestDto("json-java", "https://github.com/stleary/json-java")

        val responseDto = openSourceService.updateOpenSource(openSourceId, requestDto)

        // then
        assertThat(responseDto)
            .extracting("id", "name", "originUrl")
            .contains(openSourceId, requestDto.name, requestDto.originUrl)
    }

    @DisplayName("오픈소스가 존재하지 않으면 오픈소스 정보를 갱신할 수 없다.")
    @Test
    fun `cannot update open source if open source is not found`() {
        // when // then
        val openSourceId = "invalid client id"
        val requestDto = OpenSourceUpdateRequestDto("json-java", "https://github.com/stleary/json-java")
        assertThatThrownBy { openSourceService.updateOpenSource(openSourceId, requestDto) }
            .isInstanceOf(OpenSourceNotFoundException::class.java)
    }

    @DisplayName("갱신할 오픈소스 정보가 이미 존재하면 오픈소스 정보를 갱신할 수 없다.")
    @Test
    fun `cannot update open source if open source is already exist`() {
        // when // then
        val openSourceId = openSource.clientId
        val requestDto = OpenSourceUpdateRequestDto("JSON-java", "https://github.com/stleary/JSON-java")
        assertThatThrownBy { openSourceService.updateOpenSource(openSourceId, requestDto) }
            .isInstanceOf(OpenSourceAlreadyExistsException::class.java)
    }
}
