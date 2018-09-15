package net.binggl.mydms.infrastructure.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.*
import javax.servlet.http.HttpServletResponse

@Component
class HttpSecurityHeaders(@Value("\${application.security.cors}") private val corsUrl: String,
                          @Value("\${application.baseUrl}") private val appBaseUrl: String): Filter {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse,
                          chain: FilterChain) {
        val response = res as HttpServletResponse
        for ((headerName, headerValue) in Headers) {
            when (headerName) {
                "Access-Control-Allow-Origin" -> response.setHeader(headerName, if(headerName == "Access-Control-Allow-Origin" && corsUrl.isNotEmpty()) corsUrl else headerValue)
                "Content-Security-Policy" -> response.setHeader(headerName, headerValue.replace("APP_BASE_URL", appBaseUrl))
                else -> response.setHeader(headerName, headerValue)
            }
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
        // https://scotthelme.co.uk/content-security-policy-an-introduction/
        // https://scotthelme.co.uk/a-new-security-header-referrer-policy/
        private val Headers = mapOf("X-Frame-Options" to "SAMEORIGIN",
                "X-XSS-Protection" to "1; mode=block",
                "Access-Control-Allow-Origin" to "*",
                "Strict-Transport-Security" to "max-age=31536000; includeSubDomains; preload",
                "Referrer-Policy" to "same-origin",
                "Content-Security-Policy" to "default-src APP_BASE_URL; script-src 'self' APP_BASE_URL 'unsafe-inline'; style-src 'self' APP_BASE_URL 'unsafe-inline'; img-src ‘self’ APP_BASE_URL data:; object-src 'self APP_BASE_URL 'unsafe-eval'")
    }

}