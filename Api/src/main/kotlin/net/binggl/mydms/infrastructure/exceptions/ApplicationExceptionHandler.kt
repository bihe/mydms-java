package net.binggl.mydms.infrastructure.exceptions

import net.binggl.mydms.infrastructure.security.InvalidAuthenticationException
import net.binggl.mydms.infrastructure.security.InvalidAuthorizationException
import net.binggl.mydms.shared.api.ApiUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
class ApplicationExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [
        MydmsException::class,
        InvalidAuthorizationException::class,
        InvalidAuthenticationException::class
    ])
    protected fun handleConflict(ex: RuntimeException, request: WebRequest): ResponseEntity<Any> {

        var response:  ResponseEntity<Any>

        var isBrowserRequest = false
        if(request is ServletWebRequest) {
            isBrowserRequest = ApiUtils.isBrowserRequest(request.request)
        }

        when(ex) {
            is InvalidAuthorizationException -> {
                response = handleExceptionInternal(ex, ex.message,
                        HttpHeaders(), HttpStatus.UNAUTHORIZED, request)
            }
            is InvalidAuthenticationException -> {
                response = handleExceptionInternal(ex, ex.message,
                        HttpHeaders(), HttpStatus.FORBIDDEN, request)
            }
            is MydmsException -> {
                response = handleExceptionInternal(ex, ex.message,
                        HttpHeaders(), HttpStatus.BAD_REQUEST, request)
            }
            else -> {
                response = handleExceptionInternal(ex, ex.cause?.message ?: ex.message,
                        HttpHeaders(), HttpStatus.BAD_REQUEST, request)
            }
        }
        return response
    }
}