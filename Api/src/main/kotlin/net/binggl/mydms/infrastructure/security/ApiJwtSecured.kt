package net.binggl.mydms.infrastructure.security

import net.binggl.mydms.shared.models.Role
import java.lang.annotation.Documented
import java.lang.annotation.Inherited

@Documented
@Inherited
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiJwtSecured(val url: String, val requiredRole: Role = Role.None)