package net.binggl.mydms.features.records.models

data class Tag(val id: Long?, val name: String) {
    constructor(name: String) : this(null, name)
}
