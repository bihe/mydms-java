package net.binggl.mydms.infrastructure.error

import net.binggl.mydms.shared.application.AppVersionInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.ModelAndView
import java.time.Year

@Controller
class MydmsErrorController(@Autowired private val errorAttributes: ErrorAttributes,
                           @Value("\${application.detailedErrors}") private val detailedErrors: Boolean) : ErrorController {

    @RequestMapping(value = [PATH])
    fun error(request: WebRequest): ModelAndView {
        val mv =  ModelAndView("error", this.getErrorAttributes(request, detailedErrors))
        mv.model["year"] = Year.now()
        mv.model["appName"] = AppVersionInfo.versionInfo.artifactId
        return mv
    }

    override fun getErrorPath(): String {
        return PATH
    }


    private fun getErrorAttributes(request: WebRequest, includeStackTrace: Boolean): Map<String, Any> {
        return errorAttributes.getErrorAttributes(request, true)
    }

    companion object {
        private const val PATH = "/error"
    }
}