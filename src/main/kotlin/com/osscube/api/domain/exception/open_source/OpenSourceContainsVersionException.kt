package com.osscube.api.domain.exception.open_source

import com.osscube.api.domain.exception.errorCode.OpenSourceErrorCode
import com.osscube.api.domain.exception.upper.OpenSourceException
import org.springframework.http.HttpStatus

class OpenSourceContainsVersionException : OpenSourceException(
    status = HttpStatus.CONFLICT,
    errorCode = OpenSourceErrorCode.CONTAINS_VERSIONS,
    message = "오픈소스에 등록된 버전이 존재합니다."
)
