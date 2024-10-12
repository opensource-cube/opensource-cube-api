package com.osscube.api.domain.model.repository.open_source_repository

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.repository.OpenSourceRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FindByClientIdTest : TestContainers() {
    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    private lateinit var given: OpenSource

    @BeforeEach
    fun init() {
        // given
        given = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(given)
    }

    @AfterEach
    fun cleansing() {
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("clientId에 해당하는 오픈소스를 조회한다.")
    @Test
    fun findOpenSourceByClientId() {
        // when
        val openSource = openSourceRepository.findByClientId(given.clientId)

        // then
        assertThat(openSource)
            .isNotNull
            .extracting("id", "clientId", "name", "originUrl")
            .contains(given.id, given.clientId, given.name, given.originUrl)
    }

    @DisplayName("clientId에 해당하는 오픈소스가 존재하지 않으면 조회할 수 없다.")
    @Test
    fun cannotFindOpenSourceIfClientIdNotExists() {
        // when
        val openSource = openSourceRepository.findByClientId("invalid client id")

        // then
        assertThat(openSource).isNull()
    }
}
