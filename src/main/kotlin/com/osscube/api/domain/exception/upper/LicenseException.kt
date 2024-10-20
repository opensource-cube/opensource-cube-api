package com.osscube.api.domain.exception.upper

import com.osscube.api.domain.exception.errorCode.LicenseErrorCode
import org.springframework.http.HttpStatus

open class LicenseException(
    val status: HttpStatus,
    errorCode: LicenseErrorCode,
    message: String
) : RuntimeException(message) {
    val errorCode = String.format("LICENSE-%03d", errorCode.ordinal + 1)
}
