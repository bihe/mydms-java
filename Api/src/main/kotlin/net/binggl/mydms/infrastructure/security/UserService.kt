package net.binggl.mydms.infrastructure.security

import net.binggl.mydms.shared.models.User
import org.springframework.stereotype.Service

@Service
class UserService {
    val currentUser = ThreadLocal<User>()

    fun clearCurrentUser() = currentUser.remove()

    fun setCurrentUser(user: User): User {
        currentUser.set(user)
        return user
    }
}