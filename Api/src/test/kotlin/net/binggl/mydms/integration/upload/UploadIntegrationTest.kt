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
        val tempFile = Files.createTempFile("test", ".pdf")
        Files.write(tempFile, "testcontent".toByteArray())

        val pdfHeader = HttpHeaders()
        pdfHeader.contentType = MediaType.TEXT_PLAIN
        val pdfPart = HttpEntity(FileSystemResource(tempFile.toFile()), pdfHeader)
        val bodyMap = LinkedMultiValueMap<String, Any>()
        bodyMap.add("file", pdfPart)

        val requestEntity = HttpEntity(bodyMap, IntegrationHelpers.headers)

        val response = this.restTemplate
                .exchange("/api/v1/upload/file", HttpMethod.POST, requestEntity, String::class.java)

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        Assert.assertTrue(response.body == "The supplied mime-type is not allowed: text/plain")
    }
}