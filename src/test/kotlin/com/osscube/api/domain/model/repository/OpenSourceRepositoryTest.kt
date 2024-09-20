package com.osscube.api.domain.model.repository

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.model.entity.OpenSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class OpenSourceRepositoryTest : TestContainers() {
    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @BeforeEach
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
}
