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
class ExistsByNameAndOriginUrlTest : TestContainers() {
    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    private lateinit var openSource: OpenSource

    @BeforeEach
    fun init() {
        // given
        openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)
    }

    @AfterEach
    fun cleansing() {
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("오픈소스가 없으면 false를 반환한다.")
    @Test
    fun existsByNameAndOriginUrlReturnFalseIfNotExist() {
        // when
        val otherOpenSource = OpenSource("openssl", "https://github.com/openssl/openssl")
        val areExist = listOf(
            openSourceRepository.existsByNameAndOriginUrl(openSource.name, otherOpenSource.originUrl),
            openSourceRepository.existsByNameAndOriginUrl(otherOpenSource.name, openSource.originUrl),
            openSourceRepository.existsByNameAndOriginUrl(otherOpenSource.name, otherOpenSource.originUrl)
        )

        // then
        areExist.forEach { isExist -> assertThat(isExist).isFalse() }
    }

    @DisplayName("오픈소스가 이미 저장되어 있으면 true를 반환한다.")
    @Test
    fun existsByNameAndOriginUrlReturnTrueIfExist() {
        // when
        val isExist = openSourceRepository.existsByNameAndOriginUrl(openSource.name, openSource.originUrl)

        // then
        assertThat(isExist).isTrue()
    }
}
