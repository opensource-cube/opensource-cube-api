package com.osscube.api.domain.model.repository.license_repository

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.model.entity.License
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.LicenseRepository
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FindByOpenSourceVersionAndClientIdTest : TestContainers() {
    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @Autowired
    private lateinit var openSourceVersionRepository: OpenSourceVersionRepository

    @Autowired
    private lateinit var licenseRepository: LicenseRepository

    private lateinit var openSourceVersions: List<OpenSourceVersion>
    private lateinit var license: License

    @BeforeEach
    fun init() {
        // given
        val openSource = OpenSource("JSON-java", "https://github.com/stleary/JSON-java")
        openSourceRepository.save(openSource)

        openSourceVersions = listOf(
            OpenSourceVersion(openSource, "20240303", "https://github.com/stleary/JSON-java/archive/refs/tags/20240303.tar.gz"),
            OpenSourceVersion(openSource, "20240205", "https://github.com/stleary/JSON-java/archive/refs/tags/20240205.tar.gz")
        )
        openSourceVersionRepository.saveAll(openSourceVersions)

        license = License(openSourceVersions[0], "JSON License", "temp")
        licenseRepository.save(license)
    }

    @AfterEach
    fun cleansing() {
        licenseRepository.deleteAllInBatch()
        openSourceVersionRepository.deleteAllInBatch()
        openSourceRepository.deleteAllInBatch()
    }

    @DisplayName("오픈스 버전과 라이선스 아이디로 라이선스를 조회한다.")
    @Test
    fun `find license by open source version and client id`() {
        // when
        val openSourceVersion = openSourceVersions[0]
        val clientId = license.clientId
        val license = licenseRepository.findByOpenSourceVersionAndClientId(openSourceVersion, clientId)!!

        // then
        assertThat(license)
            .isNotNull
            .extracting("openSourceVersion.id", "clientId", "type")
            .containsExactly(openSourceVersion.id, license.clientId, license.type)
    }

    @DisplayName("라이선스 아이디가 존재하지 않으면 라이선스를 조회할 수 없다.")
    @Test
    fun `cannot find license if client id is not exist`() {
        // when
        val openSourceVersion = openSourceVersions[0]
        val clientId = "invalid client id"
        val license = licenseRepository.findByOpenSourceVersionAndClientId(openSourceVersion, clientId)

        // then
        assertThat(license).isNull()
    }

    @DisplayName("오픈소스 버전에 해당 라이선스 아이디가 존재하지 않으면 라이선스를 조회할 수 없다.")
    @Test
    fun `cannot find license if client id is not exist with open source version`() {
        // when
        val openSourceVersion = openSourceVersions[1]
        val clientId = license.clientId
        val license = licenseRepository.findByOpenSourceVersionAndClientId(openSourceVersion, clientId)

        // then
        assertThat(license).isNull()
    }
}
