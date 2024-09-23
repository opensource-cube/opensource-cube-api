package com.osscube.api.domain.exception.upper

import com.osscube.api.domain.exception.errorCode.OpenSourceVersionErrorCode
import org.springframework.http.HttpStatus

open class OpenSourceVersionException(
    val status: HttpStatus,
    errorCode: OpenSourceVersionErrorCode,
    message: String
) : RuntimeException(message) {
    val errorCode = String.format("OPEN_SOURCE_VERSION-%03d", errorCode.ordinal + 1)
}
