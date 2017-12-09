package net.binggl.mydms.features.startpage

import net.binggl.mydms.infrastructure.security.ApiSecured
import net.binggl.mydms.shared.application.AppVersionInfo
import net.binggl.mydms.shared.models.Role
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import java.time.Year

@Controller
class HomeController(@Value("\${auth.loginUrl}") private val loginUrl: String,
        @Value("\${application.url}") private val applicationUrl: String) {


    @ApiSecured(requiredRole = Role.User)
    @GetMapping("/")
    fun index(): ModelAndView {
        return ModelAndView("redirect:$applicationUrl")
    }

    @GetMapping("/login")
    fun login(model: Model): String {
        model.addAttribute("year", Year.now())
        model.addAttribute("appName", AppVersionInfo.versionInfo.artifactId)
        model.addAttribute("loginUrl", loginUrl)
        return "login"
    }
}