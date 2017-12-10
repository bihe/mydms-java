package net.binggl.mydms.features.tags

data class Tag(val id: Long?, val name: String) {
    constructor(name: String) : this(null, name)
}
