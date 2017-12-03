package net.binggl.mydms.shared.api

import net.binggl.mydms.infrastructure.security.UserService
import net.binggl.mydms.shared.models.User
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseResource {

    @Autowired
    private lateinit var userService: UserService

    val user: User
        get() = this.userService.currentUser.get()
}