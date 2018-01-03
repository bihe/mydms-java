package net.binggl.mydms.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "application.upload")
class UploadConfig {

    var allowedFileTypes: List<String> = emptyList()
    var maxUploadSize: Long = 0
    var uploadPath: String = ""
}