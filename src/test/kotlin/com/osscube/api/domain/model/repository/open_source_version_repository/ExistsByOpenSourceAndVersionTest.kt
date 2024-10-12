package com.osscube.api.domain.model.repository.open_source_version_repository

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ExistsByOpenSourceAndVersionTest : TestContainers() {
    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @Autowired
    private lateinit var openSourceVersionRepository: OpenSourceVersionRepository

    @AfterEach
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
}
