package com.osscube.api.domain.model.repository

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.model.entity.OpenSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class OpenSourceRepositoryTest : TestContainers() {
    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @AfterEach
    fun cleansing() {
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("오픈소스가 없으면 false를 반환한다.")
    @Test
    fun existsByNameAndOriginUrlReturnFalseIfNotExist() {
        // given
        val openSource = OpenSource("name", "origin url")
        openSourceRepository.save(openSource)

        // when
        val areExist = listOf(
            openSourceRepository.existsByNameAndOriginUrl(openSource.name, "other origin url"),
            openSourceRepository.existsByNameAndOriginUrl("other name", openSource.originUrl),
            openSourceRepository.existsByNameAndOriginUrl("other name", "otherOrigin url")
        )

        // then
        areExist.forEach { isExist -> assertThat(isExist).isFalse() }
    }

    @DisplayName("오픈소스가 이미 저장되어 있으면 true를 반환한다.")
    @Test
    fun existsByNameAndOriginUrlReturnTrueIfExist() {
        // given
        val openSource = OpenSource("name", "origin url")
        openSourceRepository.save(openSource)

        // when
        val isExist = openSourceRepository.existsByNameAndOriginUrl(openSource.name, openSource.originUrl)

        // then
        assertThat(isExist).isTrue()
    }

    @DisplayName("clientId에 해당하는 오픈소스를 조회한다.")
    @Test
    fun findOpenSourceByClientId() {
        // given
        val given = OpenSource("name", "origin url")
        openSourceRepository.save(given)

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
        // given
        val given = OpenSource("name", "origin url")
        openSourceRepository.save(given)

        // when
        val openSource = openSourceRepository.findByClientId("invalid client id")

        // then
        assertThat(openSource).isNull()
    }
}
