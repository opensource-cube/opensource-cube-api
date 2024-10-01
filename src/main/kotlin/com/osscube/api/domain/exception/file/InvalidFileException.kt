package com.osscube.api.domain.exception.file

import com.osscube.api.domain.exception.errorCode.FileErrorCode
import com.osscube.api.domain.exception.upper.FileException
import org.springframework.http.HttpStatus

class InvalidFileException : FileException(
    status = HttpStatus.NOT_FOUND,
    errorCode = FileErrorCode.INVALID_MIME_TYPE,
    message = "유효하지 않은 파일입니다."
)
