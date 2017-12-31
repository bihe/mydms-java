package net.binggl.mydms.shared.models

enum class Role(val precedence: Int) {
    None(-1),
    User(1),
    Admin(99);

    companion object {
        fun fromString(text: String?): Role {
            if (text != null && "" != text) {
                Role.values()
                        .filter { text.equals(it.name, ignoreCase = true) }
                        .forEach { return it }
            }
            return Role.None
        }
    }
}