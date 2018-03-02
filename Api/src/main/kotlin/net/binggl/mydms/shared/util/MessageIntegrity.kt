package net.binggl.mydms.shared.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class MessageIntegrity(@Value("\${application.security.secret}") private val secret: String) {

    fun generateValidMessage(message: String): Optional<Message> {
        if(StringUtils.isEmpty(message)) {
            return Optional.empty()
        }
        return Optional.of(Message(message = message, hash = getHash(message)))
    }

    fun serialize(message: Message): String {
        return objectMapper.writeValueAsString(message)
    }

    fun deserialize(data: String): Optional<Message> {
        val message = objectMapper.readValue(data, Message::class.java)
        if (isMessageValid(message)) {
            return Optional.of(message)
        }
        return Optional.empty()
    }

    private fun isMessageValid(message: Message): Boolean {
        val checkHash = getHash(message.message)
        return checkHash == message.hash
    }

    private fun getHash(message: String): String {
        val payload = "$message|$secret"
        return DigestUtils.sha256Hex(payload)
    }

    companion object {
        val objectMapper = jacksonObjectMapper()
    }
}