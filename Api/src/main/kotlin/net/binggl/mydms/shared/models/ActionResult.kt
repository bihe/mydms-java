package net.binggl.mydms.shared.models

enum class ActionResult constructor(private val id: Int, private val resultName: String) {
    None(0, "None"),
    Created(1, "Saved"),
    Updated(2, "Found"),
    Deleted(3, "Deleted"),
    Error(99, "Error")
}