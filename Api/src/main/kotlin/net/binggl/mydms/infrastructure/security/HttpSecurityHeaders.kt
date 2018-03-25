package net.binggl.mydms.infrastructure.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.*
import javax.servlet.http.HttpServletResponse

@Component
class HttpSecurityHeaders(@Value("\${application.security.cors}") private val corsUrl: String): Filter {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse,
                          chain: FilterChain) {
        val response = res as HttpServletResponse
        for ((headerName, headerValue) in Headers) {
            val value = if(headerName == "Access-Control-Allow-Origin" && corsUrl.isNotEmpty()) corsUrl else headerValue
            response.setHeader(headerName, value)
        }
        chain.doFilter(req, res)
    }

    override fun destroy() {}

    @Throws(ServletException::class)
    override fun init(arg0: FilterConfig) {
    }

    companion object {
        // best practise headers
        // https://www.keycdn.com/blog/http-security-headers/
        private val Headers = mapOf("X-Frame-Options" to "SAMEORIGIN",
                "X-XSS-Protection" to "1; mode=block",
                "Access-Control-Allow-Origin" to "*",
                "strict-transport-security" to "max-age=31536000; includeSubDomains; preload")
    }

}