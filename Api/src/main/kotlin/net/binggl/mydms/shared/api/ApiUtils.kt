package net.binggl.mydms.shared.api

import javax.servlet.http.HttpServletRequest

object ApiUtils {

    private const val JSON_MEDIA_TYPE = "application/json"
    private const val XML_MEDIA_TYPE = "text/xml"

    fun isBrowserRequest(request: HttpServletRequest): Boolean {

        var treatAsBrowser = true

        if(request.contentType == JSON_MEDIA_TYPE || request.contentType == XML_MEDIA_TYPE) {
            !treatAsBrowser
        }

        return treatAsBrowser;
    }
}