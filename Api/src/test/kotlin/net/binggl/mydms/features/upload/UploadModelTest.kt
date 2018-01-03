package net.binggl.mydms.features.upload

import FixtureHelpers
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import net.binggl.mydms.features.upload.models.UploadItem
import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat

class UploadModelTest {

    @Test
    fun serializeToJson(): Unit {
        val uploadItem = UploadItem("id", "test", "mime", simpleDateFormat.parse("01.01.2018"))
        val expected = MAPPER.writeValueAsString(MAPPER.readValue(FixtureHelpers.fixture("fixtures/UploadItem.json"), UploadItem::class.java))
        Assert.assertEquals(expected, MAPPER.writeValueAsString(uploadItem))
    }

    @Test
    fun deserializeFromJson(): Unit {
        val uploadItem = UploadItem("id", "test", "mime", simpleDateFormat.parse("01.01.2018"))
        Assert.assertEquals(MAPPER.readValue(FixtureHelpers.fixture("fixtures/UploadItem.json"), UploadItem::class.java), uploadItem)
    }

    companion object {
        // https://github.com/FasterXML/jackson-module-kotlin
        private val MAPPER = jacksonObjectMapper()
        private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
    }
}