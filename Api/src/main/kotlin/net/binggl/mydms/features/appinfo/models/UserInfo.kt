package net.binggl.mydms.features.appinfo.models

import net.binggl.mydms.shared.models.Claim
import net.binggl.mydms.shared.models.User
import java.io.Serializable

class UserInfo(val userId: String, val userName: String, val displayName: String, val claims: List<Claim>?) : Serializable {

    constructor(user: User) : this(user.userId, user.userName, user.displayName, user.claims){
    }

    companion object {
        private val serialVersionUID = 1L
    }
}