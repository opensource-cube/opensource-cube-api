package com.osscube.api.application.exception

import com.osscube.api.common.response.ErrorResponse
import com.osscube.api.domain.exception.license.LicenseNotFoundException
import com.osscube.api.domain.exception.upper.LicenseException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class LicenseExceptionHandler {
    @ExceptionHandler(LicenseNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleOpenSourceNotFoundException(exception: LicenseException) =
        ErrorResponse(exception.status, exception.errorCode, exception.message!!)
}
