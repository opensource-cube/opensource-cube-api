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
import org.assertj.core.api.Assertions.tuple
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
        val clientId = openSource.clientId
        val requestDto = OpenSourceVersionAddRequestDto("20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")
        val responseDto = openSourceVersionService.addNewVersion(clientId, requestDto)

        // then
        assertThat(responseDto.id)
            .hasSize(36)
        assertThat(responseDto)
            .extracting("version", "sourceUrl")
            .contains(requestDto.version, requestDto.sourceUrl)
    }

    @DisplayName("오픈소스가 없으면 새로운 버전을 추가할 수 없다.")
    @Test
    fun cannotAddNewVersionIfOpenSourceNotExist() {
        // given

        // when // then
        val requestDto = OpenSourceVersionAddRequestDto("v1.0.0", null)
        assertThatThrownBy { openSourceVersionService.addNewVersion("invalid client id", requestDto) }
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
        val clientId = openSource.clientId
        val requestDto = OpenSourceVersionAddRequestDto(version, sourceUrl)
        assertThatThrownBy { openSourceVersionService.addNewVersion(clientId, requestDto) }
            .isInstanceOf(OpenSourceVersionAlreadyExistsException::class.java)
    }

    @DisplayName("openSourceId에 해당하는 오픈소스의 모든 버전 조회하기")
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

        // when
        val openSourceVersions = openSourceVersionService.getVersions(openSource.clientId)

        // then
        openSourceVersions.forEach { version -> assertThat(version.id).hasSize(36) }
        assertThat(openSourceVersions)
            .hasSize(3)
            .extracting("version", "sourceUrl")
            .contains(
                tuple("20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz"),
                tuple("20240205", "https://github.com/stleary/JSON-java/archive/refs/tags/20240205.tar.gz"),
                tuple("20231013", null)
            )
    }

    @DisplayName("오픈소스가 존재하지 않으면 버전을 조회할 수 없다.")
    @Test
    fun cannotGetVersionsIfOpenSourceNotExists() {
        // given

        // when // then
        assertThatThrownBy { openSourceVersionService.getVersions("invalid open source id") }
            .isInstanceOf(OpenSourceNotFoundException::class.java)
    }
}
