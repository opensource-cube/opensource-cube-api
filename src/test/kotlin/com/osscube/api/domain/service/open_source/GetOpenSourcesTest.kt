package com.osscube.api.domain.service.open_source

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.service.OpenSourceService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GetOpenSourcesTest : TestContainers() {
    @Autowired
    private lateinit var openSourceService: OpenSourceService

    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @AfterEach
    fun cleansing() {
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("모든 오픈소스를 조회한다.")
    @Test
    fun getOpenSources() {
        // given
        val given = listOf(
            OpenSource("name1", "origin url"),
            OpenSource("name2", "origin url"),
            OpenSource("name3", "origin url")
        )
        openSourceRepository.saveAll(given)

        // when
        val openSources = openSourceService.getOpenSources()

        // then
        openSources.forEach { assertThat(it.openSourceId).hasSize(36) }
        assertThat(openSources)
            .hasSize(3)
            .extracting("name", "originUrl")
            .contains(
                tuple("name1", "origin url"),
                tuple("name2", "origin url"),
                tuple("name3", "origin url")
            )
    }

    @DisplayName("오픈소스가 존재하지 않는 경우 빈 목록을 조회한다.")
    @Test
    fun getEmptyListIfOpenSourceNotExists() {
        // given

        // when
        val openSources = openSourceService.getOpenSources()

        // then
        assertThat(openSources)
            .hasSize(0)
    }
}
