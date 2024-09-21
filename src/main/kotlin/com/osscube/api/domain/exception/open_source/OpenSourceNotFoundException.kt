package com.osscube.api.domain.exception.open_source

import com.osscube.api.domain.exception.errorCode.OpenSourceErrorCode
import com.osscube.api.domain.exception.upper.OpenSourceException
import org.springframework.http.HttpStatus

class OpenSourceNotFoundException : OpenSourceException(
    status = HttpStatus.NOT_FOUND,
    errorCode = OpenSourceErrorCode.NOT_FOUND,
    message = "오픈소스를 찾을 수 없습니다."
)
