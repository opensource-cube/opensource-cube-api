package com.osscube.api.domain.service.open_source_version

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.exception.open_source.OpenSourceNotFoundException
import com.osscube.api.domain.exception.open_source_version.OpenSourceVersionNotFoundException
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
import com.osscube.api.domain.service.OpenSourceVersionService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GetVersionTest : TestContainers() {
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

    @DisplayName("오픈소스 id와 버전 id로 특정 버전의 오픈소스를 조회한다.")
    @Test
    fun getVersion() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        val openSourceVersion = OpenSourceVersion(openSource, "20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")
        openSourceVersionRepository.save(openSourceVersion)

        // when
        val openSourceId = openSource.clientId
        val openSourceVersionId = openSourceVersion.clientId
        val responseDto = openSourceVersionService.getVersion(openSourceId, openSourceVersionId)

        // then
        assertThat(responseDto)
            .extracting("id", "version", "sourceUrl")
            .contains(openSourceVersion.clientId, openSourceVersion.version, openSourceVersion.sourceUrl)
    }

    @DisplayName("오픈소스 id에 해당하는 오픈소스가 존재하지 않으면 특정 버전의 오픈소스를 조회할 수 없다.")
    @Test
    fun cannotGetVersionIfOpenSourceNotExists() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        val givenVersion = OpenSourceVersion(openSource, "20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")
        openSourceVersionRepository.save(givenVersion)

        // when // then
        val invalidOpenSourceId = "invalid open source id"
        val openSourceVersionId = givenVersion.clientId
        assertThatThrownBy { openSourceVersionService.getVersion(invalidOpenSourceId, openSourceVersionId) }
            .isInstanceOf(OpenSourceNotFoundException::class.java)
    }

    @DisplayName("오픈소스에 특정 오픈소스 버전 id를 갖는 버전의 오픈소스가 존재하지 않으면 그 버전의 오픈소스를 조회할 수 없다.")
    @Test
    fun cannotGetVersionIfOpenSourceVersionIdNotExists() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        val givenVersion = OpenSourceVersion(openSource, "20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")
        openSourceVersionRepository.save(givenVersion)

        // when // then
        val openSourceId = openSource.clientId
        val invalidOpenSourceVersionId = "invalid open source version id"
        assertThatThrownBy { openSourceVersionService.getVersion(openSourceId, invalidOpenSourceVersionId) }
            .isInstanceOf(OpenSourceVersionNotFoundException::class.java)
    }
}
