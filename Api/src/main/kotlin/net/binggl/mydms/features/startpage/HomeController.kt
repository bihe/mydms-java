package net.binggl.mydms.features.startpage

import net.binggl.mydms.infrastructure.security.ApiSecured
import net.binggl.mydms.shared.application.AppVersionInfo
import net.binggl.mydms.shared.models.Role
import net.binggl.mydms.shared.util.MessageIntegrity
import net.binggl.mydms.shared.util.fromBase64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import java.time.Year
import java.util.*



@Controller
class HomeController(@Value("\${auth.loginUrl}") private val loginUrl: String,
                     @Value("\${application.fullUrl}") private val applicationFullUrl: String,
                     @Value("\${application.spaForwardUrl}") private val spaForwardUrl: String,
                     @Autowired private val msgIntegrity: MessageIntegrity) {

    @ApiSecured(requiredRole = Role.User)
    @RequestMapping(value = "/**/{[path:[^\\.]*}")
    fun redirect(): String {
        return "forward:$spaForwardUrl"
    }

    @ApiSecured(requiredRole = Role.User)
    @GetMapping("/")
    fun index(): ModelAndView {
        return ModelAndView("redirect:$applicationFullUrl")
    }

    @GetMapping("/login/{message}", "/login")
    fun login(model: Model, @PathVariable message: Optional<String>): String {
        if (message.isPresent) {
            val msg = msgIntegrity.deserialize(message.get().fromBase64())
            if (msg.isPresent) {
                model.addAttribute("loginInfo", msg.get().message)
            }
        }
        model.addAttribute("year", Year.now())
        model.addAttribute("appName", AppVersionInfo.versionInfo.artifactId)
        model.addAttribute("loginUrl", loginUrl)
        return "login"
    }
}