package net.binggl.mydms.features.records

import FixtureHelpers
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import net.binggl.mydms.features.records.model.Sender
import org.junit.Assert
import org.junit.Test

class SenderModelTest {

    @Test
    fun serializeToJson() {
        val tag = Sender(id = 1, name = "sender1")
        val expected = MAPPER.writeValueAsString(MAPPER.readValue(FixtureHelpers.fixture("fixtures/sender.json"), Sender::class.java))
        Assert.assertEquals(expected, MAPPER.writeValueAsString(tag))
    }

    @Test
    fun deserializeFromJson() {
        val tag = Sender(id = 1, name = "sender1")
        Assert.assertEquals(MAPPER.readValue(FixtureHelpers.fixture("fixtures/sender.json"), Sender::class.java), tag)
    }

    companion object {
        // https://github.com/FasterXML/jackson-module-kotlin
        private val MAPPER = jacksonObjectMapper()
    }
}