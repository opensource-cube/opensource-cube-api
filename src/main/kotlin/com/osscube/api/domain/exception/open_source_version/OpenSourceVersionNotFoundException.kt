package com.osscube.api.domain.exception.open_source_version

import com.osscube.api.domain.exception.errorCode.OpenSourceVersionErrorCode
import com.osscube.api.domain.exception.upper.OpenSourceVersionException
import org.springframework.http.HttpStatus

class OpenSourceVersionNotFoundException : OpenSourceVersionException(
    status = HttpStatus.NOT_FOUND,
    errorCode = OpenSourceVersionErrorCode.NOT_FOUND,
    message = "특정 버전의 오픈소스가 존재하지 않습니다."
)
