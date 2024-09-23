package com.osscube.api.domain.service

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.dto.OpenSourceVersionAddRequestDto
import com.osscube.api.domain.exception.open_source.OpenSourceNotFoundException
import com.osscube.api.domain.exception.open_source_version.OpenSourceVersionAlreadyExistsException
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
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
class OpenSourceVersionServiceTest : TestContainers() {
    @Autowired
    private lateinit var openSourceVersionService: OpenSourceVersionService

    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @Autowired
    private lateinit var openSourceVersionRepository: OpenSourceVersionRepository

    @BeforeEach
    fun cleansing() {
        openSourceVersionRepository.deleteAllInBatch()
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("오픈소스에 새로운 버전을 추가한다.")
    @Test
    fun addNewVersion() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        // when
        val version = "20240303"
        val sourceUrl = "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz"
        val requestDto = OpenSourceVersionAddRequestDto(openSource.clientId, version, sourceUrl)
        val responseDto = openSourceVersionService.addNewVersion(requestDto)

        // then
        assertThat(responseDto.id)
            .hasSize(36)
        assertThat(responseDto)
            .extracting("version", "sourceUrl")
            .contains(version, sourceUrl)
    }

    @DisplayName("오픈소스가 없으면 새로운 버전을 추가할 수 없다.")
    @Test
    fun cannotAddNewVersionIfOpenSourceNotExist() {
        // given

        // when // then
        val requestDto = OpenSourceVersionAddRequestDto("invalid client id", "v1.0.0", null)
        assertThatThrownBy { openSourceVersionService.addNewVersion(requestDto) }
            .isInstanceOf(OpenSourceNotFoundException::class.java)
    }

    @DisplayName("오픈소스 이미 추가된 버전을 다시 추가할 수 없다.")
    @Test
    fun cannotAddNewVersionIfAlreadyAdded() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        val version = "20240303"
        val sourceUrl = "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz"
        val openSourceVersion = OpenSourceVersion(openSource, version, sourceUrl)
        openSourceVersionRepository.save(openSourceVersion)

        // when // then
        val requestDto = OpenSourceVersionAddRequestDto(openSource.clientId, version, sourceUrl)
        assertThatThrownBy { openSourceVersionService.addNewVersion(requestDto) }
            .isInstanceOf(OpenSourceVersionAlreadyExistsException::class.java)
    }
}
