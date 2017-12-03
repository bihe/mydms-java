package net.binggl.mydms.infrastructure

import  org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val HEADER_NAME = "X-Clacks-Overhead"

@Component
class XClacksOverhead : Filter {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse,
                          chain: FilterChain) {

        val response = res as HttpServletResponse
        val request = req as HttpServletRequest

        response.setHeader(HEADER_NAME, "GNU Terry Pratchett")


        chain.doFilter(req, res)
    }

    override fun destroy() {}

    @Throws(ServletException::class)
    override fun init(arg0: FilterConfig) {
    }
}