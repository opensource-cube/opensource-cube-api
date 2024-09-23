package com.osscube.api.application.exception

import com.osscube.api.common.response.ErrorResponse
import com.osscube.api.domain.exception.open_source.OpenSourceAlreadyExistsException
import com.osscube.api.domain.exception.open_source.OpenSourceNotFoundException
import com.osscube.api.domain.exception.upper.OpenSourceException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class OpenSourceExceptionHandler {
    @ExceptionHandler(OpenSourceNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleOpenSourceNotFoundException(exception: OpenSourceException) =
        ErrorResponse(exception.status, exception.errorCode, exception.message!!)

    @ExceptionHandler(OpenSourceAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleOpenSourceAlreadyExistException(exception: OpenSourceException) =
        ErrorResponse(exception.status, exception.errorCode, exception.message!!)
}
