package net.binggl.mydms.infrastructure.exceptions

import net.binggl.mydms.features.gdrive.GDriveRuntimeException
import net.binggl.mydms.shared.api.ApiUtils
import net.binggl.mydms.shared.util.MessageIntegrity
import net.binggl.mydms.shared.util.toBase64
import org.springframework.beans.factory.annotation.Autowired
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
class ApplicationExceptionHandler(@Autowired private val msgIntegrity: MessageIntegrity) : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [
        MydmsException::class,
        InvalidAuthorizationException::class,
        InvalidAuthenticationException::class,
        GDriveRuntimeException::class,
        Exception::class
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
                    val message = msgIntegrity.generateValidMessage(ex.message ?: "")
                    if (message.isPresent) {
                        val msg = msgIntegrity.serialize(message = message.get())
                        ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).location(URI("/login/${msg.toBase64()}")).build()
                    } else {
                        ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).location(URI("/login")).build()
                    }
                } else
                    handleExceptionInternal(ex, ex.message, HttpHeaders(), HttpStatus.UNAUTHORIZED, request)
            }
            is InvalidAuthenticationException -> {
                response = if(isBrowserRequest) {
                    val message = msgIntegrity.generateValidMessage(ex.message ?: "")
                    if (message.isPresent) {
                        val msg = msgIntegrity.serialize(message = message.get())
                        ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).location(URI("/login/${msg.toBase64()}")).build()
                    } else {
                        ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).location(URI("/login")).build()
                    }
                } else
                    handleExceptionInternal(ex, ex.message, HttpHeaders(), HttpStatus.FORBIDDEN, request)
            }
            is GDriveRuntimeException -> {

                // TODO: provide information if this is a browser-request

                response = handleExceptionInternal(ex, ex.message,
                        HttpHeaders(), HttpStatus.BAD_GATEWAY, request)
            }
            is MydmsException -> {

                // TODO: provide information if this is a browser-request

                response = handleExceptionInternal(ex, ex.message,
                        HttpHeaders(), HttpStatus.BAD_REQUEST, request)
            }
            else -> {
                response = handleExceptionInternal(ex, ex.cause?.message ?: ex.message,
                        HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request)
            }
        }
        return response
    }
}