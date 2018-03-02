package net.binggl.mydms.infrastructure.error

import java.lang.RuntimeException

class MydmsException(message: String) : RuntimeException(message)