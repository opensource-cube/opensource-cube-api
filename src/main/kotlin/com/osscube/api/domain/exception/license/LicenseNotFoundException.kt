package com.osscube.api.domain.exception.license

import com.osscube.api.domain.exception.errorCode.LicenseErrorCode
import com.osscube.api.domain.exception.upper.LicenseException
import org.springframework.http.HttpStatus

class LicenseNotFoundException : LicenseException(
    status = HttpStatus.CONFLICT,
    errorCode = LicenseErrorCode.NOT_FOUND,
    message = "이미 추가된 오픈소스 버전입니다."
)
