package net.binggl.mydms.shared.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.io.IOException
import java.util.*

class JsonDateSerializer : JsonSerializer<Date>() {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(value: Date, gen: JsonGenerator, arg2: SerializerProvider) {
        gen.writeString(formatter.print(DateTime(value)))
    }

    companion object {

        private val formatter = ISODateTimeFormat.dateTime()
    }

}