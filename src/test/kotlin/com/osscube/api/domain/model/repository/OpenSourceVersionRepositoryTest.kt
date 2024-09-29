package com.osscube.api.domain.model.repository

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class OpenSourceVersionRepositoryTest : TestContainers() {
    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @Autowired
    private lateinit var openSourceVersionRepository: OpenSourceVersionRepository

    @BeforeEach
    fun cleansing() {
        openSourceVersionRepository.deleteAllInBatch()
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("오픈소스에 특정 버전이 있으면 true를 반환한다.")
    @Test
    fun returnTrueIfExistsByOpenSourceAndVersion() {
        // given
        val openSource = OpenSource("name", "origin url")
        openSourceRepository.save(openSource)
        val openSourceVersion = OpenSourceVersion(openSource, "v1.0.0", null)
        openSourceVersionRepository.save(openSourceVersion)

        // when
        val isExist = openSourceVersionRepository.existsByOpenSourceAndVersion(openSource, "v1.0.0")

        // then
        assertThat(isExist).isTrue()
    }

    @DisplayName("오픈소스에 특정 버전이 없으면 false를 반환한다.")
    @Test
    fun returnFalseIfExistsByOpenSourceAndVersion() {
        // given
        val openSource = OpenSource("name", "origin url")
        openSourceRepository.save(openSource)
        val otherOpenSource = OpenSource("name2", "origin url")
        openSourceRepository.save(otherOpenSource)
        val openSourceVersion = OpenSourceVersion(openSource, "v1.0.0", null)
        openSourceVersionRepository.save(openSourceVersion)

        // when

        val areExist = listOf(
            openSourceVersionRepository.existsByOpenSourceAndVersion(openSource, "v2.0.0"),
            openSourceVersionRepository.existsByOpenSourceAndVersion(otherOpenSource, "v1.0.0"),
            openSourceVersionRepository.existsByOpenSourceAndVersion(otherOpenSource, "v2.0.0"),
        )

        // then
        areExist.forEach { isExist ->
            assertThat(isExist).isFalse()
        }
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
