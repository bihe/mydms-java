package net.binggl.mydms.features.upload

import FixtureHelpers
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import net.binggl.mydms.features.upload.models.UploadResult
import net.binggl.mydms.shared.models.ActionResult
import org.junit.Assert
import org.junit.Test

class UploadResultTest {

    @Test
    fun serializeToJson(): Unit {
        val uploadResult = UploadResult("token", "message", ActionResult.Created)
        val expected = MAPPER.writeValueAsString(MAPPER.readValue(FixtureHelpers.fixture("fixtures/UploadResult.json"), UploadResult::class.java))
        Assert.assertEquals(expected, MAPPER.writeValueAsString(uploadResult))
    }

    @Test
    fun deserializeFromJson(): Unit {
        val uploadResult = UploadResult("token", "message", ActionResult.Created)
        Assert.assertEquals(MAPPER.readValue(FixtureHelpers.fixture("fixtures/UploadResult.json"), UploadResult::class.java), uploadResult)
    }

    companion object {
        // https://github.com/FasterXML/jackson-module-kotlin
        private val MAPPER = jacksonObjectMapper()
    }
}