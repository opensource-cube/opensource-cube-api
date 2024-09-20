package com.osscube.api.domain.exception.upper

import com.osscube.api.domain.exception.errorCode.OpenSourceErrorCode
import org.springframework.http.HttpStatus

open class OpenSourceException(
    val status: HttpStatus,
    errorCode: OpenSourceErrorCode,
    message: String
) : RuntimeException(message) {
    val errorCode = String.format("OPEN_SOURCE-%03d", errorCode.ordinal + 1)
}
