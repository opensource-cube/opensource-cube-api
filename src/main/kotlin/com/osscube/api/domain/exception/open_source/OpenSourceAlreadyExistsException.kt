package com.osscube.api.domain.exception.open_source

import com.osscube.api.domain.exception.errorCode.OpenSourceErrorCode
import com.osscube.api.domain.exception.upper.OpenSourceException
import org.springframework.http.HttpStatus

class OpenSourceAlreadyExistsException : OpenSourceException(
    status = HttpStatus.CONFLICT,
    errorCode = OpenSourceErrorCode.ALREADY_EXISTS,
    message = "이미 저장된 오픈소스입니다."
)
