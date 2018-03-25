package net.binggl.mydms.infrastructure.error

import net.binggl.mydms.shared.api.ApiUtils
import net.binggl.mydms.shared.util.MessageIntegrity
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ApplicationExceptionHandler(@Autowired private val msgIntegrity: MessageIntegrity) : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [
        MydmsException::class,
        Exception::class
    ])
    protected fun handle(ex: RuntimeException, request: WebRequest): ResponseEntity<Any> {

        var response:  ResponseEntity<Any>

        var isBrowserRequest = false
        if(request is ServletWebRequest) {
            isBrowserRequest = ApiUtils.isBrowserRequest(request.request)
        }

        LOG.debug("The request is a browser-request: '$isBrowserRequest'")
        LOG.error("Error occured: ${ex.message}", ex)

        when(ex) {
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

    companion object {
        private val LOG = LoggerFactory.getLogger(ApplicationExceptionHandler::class.java)
    }
}