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

    @DisplayName("모든 오픈소스를 오픈소스명을 기준으로 정렬하여 조회한다.")
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
        val openSources = openSourceService.getOpenSources()

        // then
        val orderedOpenSources = given.sortedBy { it.name }
        assertThat(openSources)
            .hasSize(given.size)
            .extracting("openSourceId", "name", "originUrl")
            .contains(
                tuple(orderedOpenSources[0].clientId, orderedOpenSources[0].name, orderedOpenSources[0].originUrl),
                tuple(orderedOpenSources[1].clientId, orderedOpenSources[1].name, orderedOpenSources[1].originUrl),
                tuple(orderedOpenSources[2].clientId, orderedOpenSources[2].name, orderedOpenSources[2].originUrl),
                tuple(orderedOpenSources[3].clientId, orderedOpenSources[3].name, orderedOpenSources[3].originUrl),
                tuple(orderedOpenSources[4].clientId, orderedOpenSources[4].name, orderedOpenSources[4].originUrl),
                tuple(orderedOpenSources[5].clientId, orderedOpenSources[5].name, orderedOpenSources[5].originUrl),
                tuple(orderedOpenSources[6].clientId, orderedOpenSources[6].name, orderedOpenSources[6].originUrl),
                tuple(orderedOpenSources[7].clientId, orderedOpenSources[7].name, orderedOpenSources[7].originUrl),
            )
    }
}
