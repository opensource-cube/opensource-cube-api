package com.osscube.api.domain.model.repository.open_source_version_repository

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ExistsByOpenSourceTest : TestContainers() {
    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @Autowired
    private lateinit var openSourceVersionRepository: OpenSourceVersionRepository

    private lateinit var openSources: List<OpenSource>

    @BeforeEach
    fun init() {
        // given
        openSources = listOf(
            OpenSource("JSON-java", "https://github.com/stleary/JSON-java"),
            OpenSource("openssl", "https://github.com/openssl/openssl")
        )
        openSourceRepository.saveAll(openSources)

        val openSource = openSources[0]
        val givenVersions = listOf(
            OpenSourceVersion(openSource, "20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz"),
            OpenSourceVersion(openSource, "20240205", "https://github.com/stleary/JSON-java/archive/refs/tags/20240205.tar.gz"),
            OpenSourceVersion(openSource, "20231013", null)
        )
        openSourceVersionRepository.saveAll(givenVersions)
    }

    @AfterEach
    fun cleansing() {
        openSourceVersionRepository.deleteAllInBatch()
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("오픈소스 버전이 존재하면 true를 반환한다.")
    @Test
    fun `return true if open source has any versions`() {
        // when
        val isExist = openSourceVersionRepository.existsByOpenSource(openSources[0])

        // then
        assertThat(isExist).isTrue()
    }

    @DisplayName("오픈소스 버전이 존재하지 않으면 false를 반환한다.")
    @Test
    fun test() {
        // when
        val isExist = openSourceVersionRepository.existsByOpenSource(openSources[1])

        // then
        assertThat(isExist).isFalse()
    }
}
