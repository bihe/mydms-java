package net.binggl.mydms.infrastructure.security

import net.binggl.mydms.shared.models.Role
import java.lang.annotation.Inherited

@MustBeDocumented
@Inherited
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiSecured(val requiredRole: Role = Role.None)