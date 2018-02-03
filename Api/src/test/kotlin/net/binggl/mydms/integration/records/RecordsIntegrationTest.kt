package net.binggl.mydms.integration.records

import net.binggl.mydms.features.records.model.Document
import net.binggl.mydms.integration.IntegrationHelpers
import net.binggl.mydms.shared.models.ActionResult
import net.binggl.mydms.shared.models.SimpleResult
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecordsIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private val document: Document
        get() {
            return Document(
                    id = "id",
                    title = "document",
                    fileName = "filename",
                    alternativeId = UUID.randomUUID().toString(),
                    previewLink = "previewLink",
                    amount = 0.0,
                    created = LocalDateTime.parse("2018-01-01T00:00:00.000", fmt),
                    modified = null,
                    tags = listOf("tag1", "tag2"),
                    senders = listOf("sender1"))
        }


    @Test
    fun saveDocument() {
        val headers = IntegrationHelpers.headers
        headers.contentType = MediaType.APPLICATION_JSON

        // payload to write
        val requestEntity = HttpEntity(this.document, headers)
        val response = this.restTemplate.postForEntity("/api/v1/documents", requestEntity, SimpleResult::class.java)

        Assert.assertEquals(HttpStatus.OK, response.statusCode)
        Assert.assertEquals(ActionResult.Created, response.body.result)
    }

    companion object {
        private val fmt = DateTimeFormatter.ISO_DATE_TIME
    }
}