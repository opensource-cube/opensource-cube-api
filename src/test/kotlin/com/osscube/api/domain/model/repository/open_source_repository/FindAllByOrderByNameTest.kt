package com.osscube.api.domain.model.repository.open_source_repository

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.repository.OpenSourceRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FindAllByOrderByNameTest : TestContainers() {
    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @AfterEach
    fun cleansing() {
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("오픈소스명을 기준으로 오픈소스 목록을 조회한다.")
    @Test
    fun `get open sources order by name`() {
        // given
        val given = listOf(
            OpenSource("JSON-java", "https://github.com/stleary/JSON-java"),
            OpenSource("openssl", "https://github.com/openssl/openssl"),
            OpenSource("spring-boot", "https://github.com/spring-projects/spring-boot"),
            OpenSource("openssh", "https://github.com/openssh"),
            OpenSource("xz", "https://github.com/tukaani-project/xz"),
            OpenSource("vscode", "https://github.com/microsoft/vscode"),
            OpenSource("jsoncpp", "https://github.com/open-source-parsers/jsoncpp"),
            OpenSource("googletest", "https://github.com/google/googletest")
        )
        openSourceRepository.saveAll(given)

        // when
        val openSources = openSourceRepository.findAllByOrderByName()

        // then
        val orderedOpenSources = given.sortedBy { it.name }
        assertThat(openSources)
            .extracting("name")
            .containsExactly(
                orderedOpenSources[0].name,
                orderedOpenSources[1].name,
                orderedOpenSources[2].name,
                orderedOpenSources[3].name,
                orderedOpenSources[4].name,
                orderedOpenSources[5].name,
                orderedOpenSources[6].name,
                orderedOpenSources[7].name,
            )
    }
}
