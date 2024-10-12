package com.osscube.api.domain.model.repository.open_source_version_repository

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FindAllByOpenSourceTest : TestContainers() {
    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @Autowired
    private lateinit var openSourceVersionRepository: OpenSourceVersionRepository

    @AfterEach
    fun cleansing() {
        openSourceVersionRepository.deleteAllInBatch()
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("오픈소스의 모든 버전을 조회한다.")
    @Test
    fun findAllByOpenSource() {
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
        val opensourceVersions = openSourceVersionRepository.findAllByOpenSource(openSource)

        // then
        opensourceVersions.forEach { version ->
            assertThat(version.openSource.id).isEqualTo(openSource.id)
            assertThat(version.clientId).hasSize(36)
        }

        assertThat(opensourceVersions)
            .hasSize(3)
            .extracting("id", "version", "sourceUrl")
            .contains(
                tuple(givenVersions[0].id, givenVersions[0].version, givenVersions[0].sourceUrl),
                tuple(givenVersions[1].id, givenVersions[1].version, givenVersions[1].sourceUrl),
                tuple(givenVersions[2].id, givenVersions[2].version, givenVersions[2].sourceUrl)
            )
    }
}
