package net.binggl.mydms.shared.util

import java.util.*

fun String.toBase64(): String {
    return Base64.getEncoder().encodeToString(this.toByteArray (Charsets.UTF_8))
}

fun String.fromBase64(): String {
    val asBytes = Base64.getDecoder().decode(this)
    return String(asBytes, Charsets.UTF_8)
}

