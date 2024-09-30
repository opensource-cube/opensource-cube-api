package com.osscube.api.application.exception

import com.osscube.api.common.response.ErrorResponse
import com.osscube.api.domain.exception.open_source_version.OpenSourceVersionAlreadyExistsException
import com.osscube.api.domain.exception.open_source_version.OpenSourceVersionNotFoundException
import com.osscube.api.domain.exception.upper.OpenSourceVersionException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class OpenSourceVersionExceptionHandler {
    @ExceptionHandler(OpenSourceVersionNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleOpenSourceVersionNotFoundException(exception: OpenSourceVersionException) =
        ErrorResponse(exception.status, exception.errorCode, exception.message!!)

    @ExceptionHandler(OpenSourceVersionAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleOpenSourceVersionAlreadyExistsException(exception: OpenSourceVersionException) =
        ErrorResponse(exception.status, exception.errorCode, exception.message!!)
}
