package net.binggl.mydms.features.tags

import FixtureHelpers
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Assert
import org.junit.Test

class TagModelTest {

    @Test
    fun serializeToJson(): Unit {
        val tag = Tag(id = 1, name = "tag1")
        val expected = MAPPER.writeValueAsString(MAPPER.readValue(FixtureHelpers.fixture("fixtures/tag.json"), Tag::class.java))
        Assert.assertEquals(expected, MAPPER.writeValueAsString(tag))
    }

    @Test
    fun deserializeFromJson(): Unit {
        val tag = Tag(id = 1, name = "tag1")
        Assert.assertEquals(MAPPER.readValue(FixtureHelpers.fixture("fixtures/tag.json"), Tag::class.java), tag)
    }

    companion object {
        // https://github.com/FasterXML/jackson-module-kotlin
        private val MAPPER = jacksonObjectMapper()
    }
}