package net.binggl.mydms.features.upload

import FixtureHelpers
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import net.binggl.mydms.features.upload.models.UploadItem
import org.joda.time.format.ISODateTimeFormat
import org.junit.Assert
import org.junit.Test

class UploadModelTest {

    @Test
    fun serializeToJson(): Unit {
        val uploadItem = UploadItem("id", "test", "mime", fmt.parseDateTime("2018-01-01T00:00:00.000+01:00").toDate())
        val expected = MAPPER.writeValueAsString(MAPPER.readValue(FixtureHelpers.fixture("fixtures/UploadItem.json"), UploadItem::class.java))
        Assert.assertEquals(expected, MAPPER.writeValueAsString(uploadItem))
    }

    @Test
    fun deserializeFromJson(): Unit {
        val uploadItem = UploadItem("id", "test", "mime", fmt.parseDateTime("2018-01-01T00:00:00.000+01:00").toDate())
        Assert.assertEquals(MAPPER.readValue(FixtureHelpers.fixture("fixtures/UploadItem.json"), UploadItem::class.java), uploadItem)
    }

    companion object {
        // https://github.com/FasterXML/jackson-module-kotlin
        private val MAPPER = jacksonObjectMapper()
        private val fmt =  ISODateTimeFormat.dateTimeParser()
    }
}