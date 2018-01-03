package net.binggl.mydms.shared.models

enum class ActionResult constructor(private val id: Int, private val resultName: String) {
    None(0, "None"),
    Created(1, "Created"),
    Updated(2, "Updated"),
    Deleted(3, "Deleted"),
    Error(99, "Error")
}