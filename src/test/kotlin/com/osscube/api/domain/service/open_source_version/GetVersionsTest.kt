package com.osscube.api.domain.service.open_source_version

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.exception.open_source.OpenSourceNotFoundException
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
import com.osscube.api.domain.service.OpenSourceVersionService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GetVersionsTest : TestContainers() {
    @Autowired
    private lateinit var openSourceVersionService: OpenSourceVersionService

    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @Autowired
    private lateinit var openSourceVersionRepository: OpenSourceVersionRepository

    @AfterEach
    fun cleansing() {
        openSourceVersionRepository.deleteAllInBatch()
        openSourceRepository.deleteAllInBatch()
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
        openSourceVersions.forEach { version -> Assertions.assertThat(version.id).hasSize(36) }
        Assertions.assertThat(openSourceVersions)
            .hasSize(3)
            .extracting("version", "sourceUrl")
            .contains(
                Assertions.tuple("20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz"),
                Assertions.tuple("20240205", "https://github.com/stleary/JSON-java/archive/refs/tags/20240205.tar.gz"),
                Assertions.tuple("20231013", null)
            )
    }

    @DisplayName("오픈소스가 존재하지 않으면 버전을 조회할 수 없다.")
    @Test
    fun cannotGetVersionsIfOpenSourceNotExists() {
        // given

        // when // then
        Assertions.assertThatThrownBy { openSourceVersionService.getVersions("invalid open source id") }
            .isInstanceOf(OpenSourceNotFoundException::class.java)
    }
}
