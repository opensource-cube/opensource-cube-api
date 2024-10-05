package com.osscube.api.application.request

import org.springframework.web.multipart.MultipartFile

class LicenseAddRequest {
    lateinit var type: String
    lateinit var file: MultipartFile
}
