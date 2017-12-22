package net.binggl.mydms.infrastructure.exceptions

import net.binggl.mydms.shared.api.ApiUtils
import net.binggl.mydms.shared.util.toBase64
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.URI


@ControllerAdvice
class ApplicationExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [
        MydmsException::class,
        InvalidAuthorizationException::class,
        InvalidAuthenticationException::class
    ])
    protected fun handle(ex: RuntimeException, request: WebRequest): ResponseEntity<Any> {

        var response:  ResponseEntity<Any>

        var isBrowserRequest = false
        if(request is ServletWebRequest) {
            isBrowserRequest = ApiUtils.isBrowserRequest(request.request)
        }

        when(ex) {
            is InvalidAuthorizationException -> {
                response = if(isBrowserRequest) {
                    ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).location(URI("/login/${ex.message?.toBase64()}")).build()
                } else
                    handleExceptionInternal(ex, ex.message, HttpHeaders(), HttpStatus.UNAUTHORIZED, request)
            }
            is InvalidAuthenticationException -> {
                response = if(isBrowserRequest) {
                    ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).location(URI("/login/${ex.message?.toBase64()}")).build()
                } else
                    handleExceptionInternal(ex, ex.message, HttpHeaders(), HttpStatus.FORBIDDEN, request)
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