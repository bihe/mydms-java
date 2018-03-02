package net.binggl.mydms.shared.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class JsonDateSerializer : JsonSerializer<LocalDateTime>() {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(value: LocalDateTime, gen: JsonGenerator, arg2: SerializerProvider) {
        gen.writeString(formatter.format(value))
    }

    companion object {
        private val formatter = DateTimeFormatter.ISO_DATE_TIME
    }

}