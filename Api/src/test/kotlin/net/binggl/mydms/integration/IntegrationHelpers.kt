package net.binggl.mydms.integration

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

const val JWT_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJlOWExZTRjY2QwOWE0Y2Y4YWE0YzEzM2U5YjM5NjkyNSIsImlhdCI6MTUxMzk2NTM0NiwiaXNzIjoibG9naW4uYmluZ2dsLm5ldCIsInN1YiI6ImxvZ2luLlVzZXIiLCJUeXBlIjoibG9naW4uVXNlciIsIlVzZXJOYW1lIjoiYmloZSIsIkVtYWlsIjoiYS5iQGMuZGUiLCJDbGFpbXMiOlsibXlkbXN8aHR0cHM6Ly9teWRtcy5iaW5nZ2wubmV0L3xVc2VyIl0sIlVzZXJJZCI6IjEyMzQiLCJEaXNwbGF5TmFtZSI6IkhlbnJpayBCaW5nZ2wifQ.gcSWaxT5MQMqXvptqoxUI6PpI5J7sNmLlcMH3fspscE"

object IntegrationHelpers {

    val headers: HttpHeaders
        get() {
            val headers = HttpHeaders()
            headers.contentType = MediaType.MULTIPART_FORM_DATA
            headers.set("Authorization", "Bearer $JWT_TOKEN")
            return headers
        }
}