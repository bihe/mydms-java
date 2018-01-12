package net.binggl.mydms.features.records

import FixtureHelpers
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import net.binggl.mydms.features.records.models.Document
import org.joda.time.format.ISODateTimeFormat
import org.junit.Assert
import org.junit.Test

class DocumentModelTest {

    @Test
    fun serializeToJson(): Unit {
        val expected = MAPPER.writeValueAsString(MAPPER.readValue(FixtureHelpers.fixture("fixtures/document.json"), Document::class.java))
        Assert.assertEquals(expected, MAPPER.writeValueAsString(document))
    }

    @Test
    fun deserializeFromJson(): Unit {
        Assert.assertEquals(MAPPER.readValue(FixtureHelpers.fixture("fixtures/document.json"), Document::class.java), document)
    }

    private val document: Document
        get() {
            return Document(id = "id", title = "document", fileName = "filename", alternativeId = "alternativeId",
                    previewLink = "previewLink", amount = 0.0, created = fmt.parseDateTime("2018-01-01T00:00:00.000+01:00").toDate(),
                    modified = fmt.parseDateTime("2018-01-01T00:00:00.000+01:00").toDate(), tags = emptyList(),
                    senders = emptyList(), uploadFileToken = null)
        }

    companion object {
        // https://github.com/FasterXML/jackson-module-kotlin
        private val MAPPER = jacksonObjectMapper()
        private val fmt =  ISODateTimeFormat.dateTimeParser()
    }
}