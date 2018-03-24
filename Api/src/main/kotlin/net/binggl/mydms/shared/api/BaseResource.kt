package net.binggl.mydms.shared.api

import net.binggl.mydms.shared.models.User
import org.springframework.security.core.context.SecurityContextHolder

abstract class BaseResource {

    val user: User
        get() = SecurityContextHolder.getContext().authentication.principal as User

}