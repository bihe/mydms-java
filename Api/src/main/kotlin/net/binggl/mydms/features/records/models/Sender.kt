package net.binggl.mydms.features.records.models

data class Sender(val id: Long?, val name: String) {
    constructor(name: String) : this(null, name)
}
