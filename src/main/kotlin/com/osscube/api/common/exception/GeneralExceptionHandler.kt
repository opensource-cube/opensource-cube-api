package com.osscube.api.common.exception

import com.osscube.api.common.response.ErrorResponse
import org.hibernate.exception.ConstraintViolationException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpServerErrorException.InternalServerError
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class GeneralExceptionHandler {
    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequestBodyException(exception: HttpMessageNotReadableException) =
        ErrorResponse(HttpStatus.BAD_REQUEST, "SYSTEM-001", "RequestBody를 제대로 읽을 수 없습니다.")

    @ExceptionHandler(MissingServletRequestParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingServletRequestParameterException(exception: MissingServletRequestParameterException) =
        ErrorResponse(HttpStatus.BAD_REQUEST, "SYSTEM-002", "${exception.parameterName}이 누락되었습니다.")

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentTypeMismatch(exception: MethodArgumentTypeMismatchException) =
        ErrorResponse(HttpStatus.BAD_REQUEST, "SYSTEM-003", "${exception.name}이 잘못 입력되었습니다.")

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleRequestValid(exception: MethodArgumentNotValidException): ErrorResponse {
        val message = exception.bindingResult.fieldErrors.joinToString("") { fieldError ->
            "[${fieldError.field}](은)는 ${fieldError.defaultMessage}"
        }
        return ErrorResponse(HttpStatus.BAD_REQUEST, "System-004", message)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleRequestValid(exception: ConstraintViolationException) =
        ErrorResponse(HttpStatus.BAD_REQUEST, "SYSTEM-005", "잘못된 데이터 요청입니다.")

    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMediaTypeNotSupportedException(exception: HttpMediaTypeNotSupportedException) =
        ErrorResponse(HttpStatus.BAD_REQUEST, "SYSTEM-006", "지원하지 않는 MediaType 입니다. 요청된 type: ${exception.contentType}")

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMethodNotSupportedException(exception: HttpRequestMethodNotSupportedException) =
        ErrorResponse(HttpStatus.BAD_REQUEST, "SYSTEM-007", "잘못된 Mapping 요청입니다.")

    @ExceptionHandler(InternalServerError::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleInternalServerError(exception: InternalServerError) =
        ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "SYSTEM-008", "서버 에러가 발생했습니다.")
}
