package net.binggl.mydms.features.records.model

data class Tag(val id: Long?, val name: String) {
    constructor(name: String) : this(null, name)
}
