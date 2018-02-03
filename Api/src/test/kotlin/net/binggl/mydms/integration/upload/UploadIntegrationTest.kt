package net.binggl.mydms.integration.upload

import net.binggl.mydms.integration.IntegrationHelpers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.io.FileSystemResource
import org.springframework.http.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.LinkedMultiValueMap
import java.nio.file.Files

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UploadIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private val jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJlOWExZTRjY2QwOWE0Y2Y4YWE0YzEzM2U5YjM5NjkyNSIsImlhdCI6MTUxMzk2NTM0NiwiaXNzIjoibG9naW4uYmluZ2dsLm5ldCIsInN1YiI6ImxvZ2luLlVzZXIiLCJUeXBlIjoibG9naW4uVXNlciIsIlVzZXJOYW1lIjoiYmloZSIsIkVtYWlsIjoiYS5iQGMuZGUiLCJDbGFpbXMiOlsibXlkbXN8aHR0cHM6Ly9teWRtcy5iaW5nZ2wubmV0L3xVc2VyIl0sIlVzZXJJZCI6IjEyMzQiLCJEaXNwbGF5TmFtZSI6IkhlbnJpayBCaW5nZ2wifQ.gcSWaxT5MQMqXvptqoxUI6PpI5J7sNmLlcMH3fspscE"

    @Test
    fun uploadTestEnd2End() {
        val tempFile = Files.createTempFile("test", ".pdf")
        val filename = tempFile.fileName.toString()
        Files.write(tempFile, "testcontent".toByteArray())

        val pdfHeader = HttpHeaders()
        pdfHeader.contentType = MediaType.APPLICATION_PDF
        val pdfPart = HttpEntity(FileSystemResource(tempFile.toFile()), pdfHeader)
        val bodyMap = LinkedMultiValueMap<String, Any>()
        bodyMap.add("file", pdfPart)

        val requestEntity = HttpEntity(bodyMap, IntegrationHelpers.headers)

        val response = this.restTemplate
                .exchange("/api/v1/upload/file", HttpMethod.POST, requestEntity, String::class.java)

        Assert.assertEquals(HttpStatus.OK, response.statusCode)
        Assert.assertTrue(response!!.body!!.indexOf("File $filename was uploaded and stored using token") > -1)
    }

    @Test
    fun uploadTestWrongContentType() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        headers.set("Authorization", "Bearer $jwtToken")

        val tempFile = Files.createTempFile("test", ".pdf")
        Files.write(tempFile, "testcontent".toByteArray())

        val pdfHeader = HttpHeaders()
        pdfHeader.contentType = MediaType.TEXT_PLAIN
        val pdfPart = HttpEntity(FileSystemResource(tempFile.toFile()), pdfHeader)
        val bodyMap = LinkedMultiValueMap<String, Any>()
        bodyMap.add("file", pdfPart)

        val requestEntity = HttpEntity(bodyMap, headers)

        val response = this.restTemplate
                .exchange("/api/v1/upload/file", HttpMethod.POST, requestEntity, String::class.java)

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        Assert.assertTrue(response.body == "The supplied mime-type is not allowed: text/plain")
    }
}