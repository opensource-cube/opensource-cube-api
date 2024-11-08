package com.osscube.api.domain.exception.license

import com.osscube.api.domain.exception.errorCode.LicenseErrorCode
import com.osscube.api.domain.exception.upper.LicenseException
import org.springframework.http.HttpStatus

class LicenseNotFoundException : LicenseException(
    status = HttpStatus.NOT_FOUND,
    errorCode = LicenseErrorCode.NOT_FOUND,
    message = "라이선스가 존재하지 않습니다."
)
