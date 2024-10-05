package com.osscube.api.domain.exception.upper

import com.osscube.api.domain.exception.errorCode.FileErrorCode
import org.springframework.http.HttpStatus

open class FileException(
    val status: HttpStatus,
    errorCode: FileErrorCode,
    message: String
) : RuntimeException(message) {
    val errorCode = String.format("FILE-%03d", errorCode.ordinal + 1)
}
