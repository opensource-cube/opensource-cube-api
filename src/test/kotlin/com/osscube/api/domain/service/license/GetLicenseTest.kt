package com.osscube.api.domain.service.license

import com.osscube.api.config.TestContainers
import com.osscube.api.domain.exception.license.LicenseNotFoundException
import com.osscube.api.domain.exception.open_source.OpenSourceNotFoundException
import com.osscube.api.domain.exception.open_source_version.OpenSourceVersionNotFoundException
import com.osscube.api.domain.model.entity.License
import com.osscube.api.domain.model.entity.OpenSource
import com.osscube.api.domain.model.entity.OpenSourceVersion
import com.osscube.api.domain.model.repository.LicenseRepository
import com.osscube.api.domain.model.repository.OpenSourceRepository
import com.osscube.api.domain.model.repository.OpenSourceVersionRepository
import com.osscube.api.domain.service.LicenseService
import com.osscube.api.utils.FileUtil
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import java.io.File
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GetLicenseTest : TestContainers() {
    @Autowired
    private lateinit var licenseService: LicenseService

    @Autowired
    private lateinit var openSourceRepository: OpenSourceRepository

    @Autowired
    private lateinit var openSourceVersionRepository: OpenSourceVersionRepository

    @Autowired
    private lateinit var licenseRepository: LicenseRepository

    private lateinit var openSources: List<OpenSource>
    private lateinit var openSourceVersions: List<OpenSourceVersion>
    private lateinit var license: License

    companion object {
        @JvmStatic
        @BeforeAll
        fun staticInit() {
            mockkObject(FileUtil)
        }

        @JvmStatic
        @AfterAll
        fun staticDestroy() {
            unmockkObject(FileUtil)
        }
    }

    @BeforeEach
    fun init() {
        // given
        openSources = listOf(
            OpenSource("JSON-java", "https://github.com/stleary/JSON-java"),
            OpenSource("openssl", "https://github.com/openssl/openssl")
        )
        openSourceRepository.saveAll(openSources)

        val openSource = openSources[0]
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

    @DisplayName("라이선스를 조회한다.")
    @Test
    fun `get license`() {
        // given
        every { FileUtil.readText(any(File::class)) } returns "Content of JSON License"

        // when
        val openSourceId = openSources[0].clientId
        val openSourceVersionId = openSourceVersions[0].clientId
        val licenseId = license.clientId
        val responseDto = licenseService.getLicense(openSourceId, openSourceVersionId, licenseId)

        // then
        assertThat(responseDto)
            .extracting("id", "type", "content")
            .containsExactly(license.clientId, license.type, "Content of JSON License")
    }

    @DisplayName("오픈소스가 존재하지 않으면 라이선스를 조회할 수 없다.")
    @Test
    fun `cannot get license if open source is not exist`() {
        // when // then
        val openSourceId = "invalid open source id"
        val openSourceVersionId = openSourceVersions[0].clientId
        val licenseId = license.clientId

        assertThatThrownBy { licenseService.getLicense(openSourceId, openSourceVersionId, licenseId) }
            .isInstanceOf(OpenSourceNotFoundException::class.java)
    }

    @DisplayName("오픈소스에 해당 버전이 존재하지 않는 경우 라이선스를 조회할 수 없다.")
    @Test
    fun `cannot get license if open source version is not in open source`() {
        // when // then
        val openSourceId = openSources[1].clientId
        val openSourceVersionId = openSourceVersions[0].clientId
        val licenseId = license.clientId

        assertThatThrownBy { licenseService.getLicense(openSourceId, openSourceVersionId, licenseId) }
            .isInstanceOf(OpenSourceVersionNotFoundException::class.java)
    }

    @DisplayName("라이선스가 해당 오픈소스 버전에 없는 경우 조회할 수 없다.")
    @Test
    fun `cannot get license if license is not in open source version`() {
        // when // then
        val openSourceId = openSources[0].clientId
        val openSourceVersionId = openSourceVersions[0].clientId
        val licenseId = "invalid license id"

        assertThatThrownBy { licenseService.getLicense(openSourceId, openSourceVersionId, licenseId) }
            .isInstanceOf(LicenseNotFoundException::class.java)
    }
}
