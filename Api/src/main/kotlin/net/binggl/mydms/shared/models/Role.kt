package net.binggl.mydms.shared.models

enum class Role constructor(val text: String) {
    None("none"), User("user"), Admin("admin");

    companion object {

        fun fromString(text: String?): Role {
            try {
                if (text != null && "" != text) {
                    for (b in Role.values()) {
                        if (text.equals(b.text, ignoreCase = true)) {
                            return b
                        }
                    }
                }
            } catch (EX: Exception) {

            }

            return Role.None
        }
    }
}