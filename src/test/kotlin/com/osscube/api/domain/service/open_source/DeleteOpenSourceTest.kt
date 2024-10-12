package com.osscube.api.domain.service.open_source

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.exception.open_source.OpenSourceContainsVersionException
import com.osscube.api.domain.exception.open_source.OpenSourceNotFoundException
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
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
class DeleteOpenSourceTest : TestContainers() {
    @Autowired
    private lateinit var openSourceService: OpenSourceService

    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @Autowired
    private lateinit var openSourceVersionRepository: OpenSourceVersionRepository

    private lateinit var openSource: OpenSource

    @BeforeEach
    fun init() {
        // given
        openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)
    }

    @AfterEach
    fun cleansing() {
        openSourceVersionRepository.deleteAllInBatch()
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("오픈소스에 버전이 등록되어 있지 않으면 오픈소스를 삭제할 수 있다.")
    @Test
    fun `can delete open source if any version is not exist`() {
        // when
        val openSourceId = openSource.clientId
        openSourceService.deleteOpenSource(openSourceId)

        // then
        assertThat(openSourceRepository.existsById(openSource.id!!)).isFalse()
    }

    @DisplayName("오픈소스가 존재하지 않으면 오픈소스를 삭제할 수 없다.")
    @Test
    fun `cannot delete open source if open source is not found`() {
        // when // then
        val openSourceId = "invalid open source id"
        assertThatThrownBy { openSourceService.deleteOpenSource(openSourceId) }
            .isInstanceOf(OpenSourceNotFoundException::class.java)
    }

    @DisplayName("오픈소스에 등록된 버전이 있으면 오픈소스를 삭제할 수 없다.")
    @Test
    fun `cannot delete open source if any version exists`() {
        // given
        val givenVersions = listOf(
            OpenSourceVersion(openSource, "20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz"),
            OpenSourceVersion(openSource, "20240205", "https://github.com/stleary/JSON-java/archive/refs/tags/20240205.tar.gz"),
            OpenSourceVersion(openSource, "20231013", null)
        )
        openSourceVersionRepository.saveAll(givenVersions)

        // when // then
        val openSourceId = openSource.clientId
        assertThatThrownBy { openSourceService.deleteOpenSource(openSourceId) }
            .isInstanceOf(OpenSourceContainsVersionException::class.java)
    }
}
