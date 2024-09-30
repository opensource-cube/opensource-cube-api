package com.osscube.api.domain.model.repository.open_source_version_repository

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class FindByOpenSourceAndClientIdTest : TestContainers() {
    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @Autowired
    private lateinit var openSourceVersionRepository: OpenSourceVersionRepository

    @BeforeEach
    fun cleansing() {
        openSourceVersionRepository.deleteAllInBatch()
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("오픈소스와 버전 id로 특정 버전의 오픈소스를 조회한다.")
    @Test
    fun findByOpenSourceAndClientId() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        val givenVersion = OpenSourceVersion(openSource, "20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")
        openSourceVersionRepository.save(givenVersion)

        // when
        val clientId = givenVersion.clientId
        val openSourceVersion = openSourceVersionRepository.findByOpenSourceAndClientId(openSource, clientId)

        // then
        assertThat(openSourceVersion)
            .extracting("id", "clientId", "version", "sourceUrl")
            .contains(givenVersion.id, givenVersion.clientId, givenVersion.version, givenVersion.sourceUrl)
    }

    @DisplayName("오픈소스가 존재하지 않으면 특정 버전의 오픈소스를 조회할 수 없다.")
    @Test
    fun cannotFindByOpenSourceAndClientIdIfOpenSourceNotExists() {
        // given
        val openSources = listOf(
            OpenSource("JSON-java", "https://github.com/stleary/JSON-java"),
            OpenSource("invalid open source", "invalid origin url")
        )
        openSourceRepository.saveAll(openSources)

        val givenVersion = OpenSourceVersion(openSources[0], "20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")
        openSourceVersionRepository.save(givenVersion)

        // when
        val clientId = givenVersion.clientId
        val openSourceVersion = openSourceVersionRepository.findByOpenSourceAndClientId(openSources[1], clientId)

        // then
        assertThat(openSourceVersion)
            .isNull()
    }

    @DisplayName("버전 id가 존재하지 않으면 특정 버전의 오픈소스를 조회할 수 없다.")
    @Test
    fun cannotFindByOpenSourceAndClientIdIfClientIdNotExists() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        val givenVersion = OpenSourceVersion(openSource, "20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")
        openSourceVersionRepository.save(givenVersion)

        // when
        val invalidClientId = "invalid client Id"
        val openSourceVersion = openSourceVersionRepository.findByOpenSourceAndClientId(openSource, invalidClientId)

        // then
        assertThat(openSourceVersion)
            .isNull()
    }
}
