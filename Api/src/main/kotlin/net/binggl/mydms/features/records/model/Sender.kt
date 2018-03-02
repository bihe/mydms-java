package net.binggl.mydms.features.records.model

data class Sender(val id: Long?, val name: String) {
    constructor(name: String) : this(null, name)
}
