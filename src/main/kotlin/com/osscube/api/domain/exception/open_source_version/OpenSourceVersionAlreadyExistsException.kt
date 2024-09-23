package com.osscube.api.domain.exception.open_source_version

import com.osscube.api.domain.exception.errorCode.OpenSourceVersionErrorCode
import com.osscube.api.domain.exception.upper.OpenSourceVersionException
import org.springframework.http.HttpStatus

class OpenSourceVersionAlreadyExistsException : OpenSourceVersionException(
    status = HttpStatus.CONFLICT,
    errorCode = OpenSourceVersionErrorCode.ALREADY_EXISTS,
    message = "이미 추가된 오픈소스 버전입니다."
)
