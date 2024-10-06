package com.osscube.api.domain.service.open_source_version

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.exception.open_source.OpenSourceNotFoundException
import com.osscube.api.domain.exception.open_source_version.OpenSourceVersionNotFoundException
import com.osscube.api.domain.model.entity.License
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.LicenseRepository
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
import com.osscube.api.domain.service.OpenSourceVersionService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
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

    @Autowired
    private lateinit var licenseRepository: LicenseRepository

    private lateinit var openSource: OpenSource
    private lateinit var openSourceVersion: OpenSourceVersion
    private lateinit var licenses: List<License>

    @BeforeEach
    fun init() {
        // given
        openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        openSourceVersion = OpenSourceVersion(openSource, "20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz")
        openSourceVersionRepository.save(openSourceVersion)

        licenses = listOf(
            License(openSourceVersion, "MIT", "temp"),
            License(openSourceVersion, "APACHE2.0", "temp")
        )
        licenseRepository.saveAll(licenses)
        openSourceVersion.licenses.addAll(licenses)
    }

    @AfterEach
    fun cleansing() {
        licenseRepository.deleteAllInBatch()
        openSourceVersionRepository.deleteAllInBatch()
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("오픈소스 id와 버전 id로 특정 버전의 오픈소스를 조회한다.")
    @Test
    fun getVersion() {
        // when
        val openSourceId = openSource.clientId
        val openSourceVersionId = openSourceVersion.clientId
        val responseDto = openSourceVersionService.getVersion(openSourceId, openSourceVersionId)

        // then
        assertThat(responseDto)
            .extracting("id", "version", "sourceUrl")
            .contains(openSourceVersion.clientId, openSourceVersion.version, openSourceVersion.sourceUrl)

        responseDto.licenses.forEach { license ->
            assertThat(license.id)
                .hasSize(36)
        }

        assertThat(responseDto.licenses)
            .extracting("type")
            .contains(licenses[0].type, licenses[1].type)
    }

    @DisplayName("오픈소스 id에 해당하는 오픈소스가 존재하지 않으면 특정 버전의 오픈소스를 조회할 수 없다.")
    @Test
    fun cannotGetVersionIfOpenSourceNotExists() {
        // when // then
        val invalidOpenSourceId = "invalid open source id"
        val openSourceVersionId = openSourceVersion.clientId
        assertThatThrownBy { openSourceVersionService.getVersion(invalidOpenSourceId, openSourceVersionId) }
            .isInstanceOf(OpenSourceNotFoundException::class.java)
    }

    @DisplayName("오픈소스에 특정 오픈소스 버전 id를 갖는 버전의 오픈소스가 존재하지 않으면 그 버전의 오픈소스를 조회할 수 없다.")
    @Test
    fun cannotGetVersionIfOpenSourceVersionIdNotExists() {
        // when // then
        val openSourceId = openSource.clientId
        val invalidOpenSourceVersionId = "invalid open source version id"
        assertThatThrownBy { openSourceVersionService.getVersion(openSourceId, invalidOpenSourceVersionId) }
            .isInstanceOf(OpenSourceVersionNotFoundException::class.java)
    }
}
