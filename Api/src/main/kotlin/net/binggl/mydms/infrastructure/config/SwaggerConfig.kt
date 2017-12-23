package net.binggl.mydms.infrastructure.config

import net.binggl.mydms.shared.application.AppVersionInfo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.util.*

@Configuration
@EnableSwagger2
class SwaggerConfig {
    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("net.binggl.mydms"))
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .apiInfo(apiInfo)
    }

    companion object {
        val apiInfo
            get() = ApiInfo("mydms API", "API for mydms application", AppVersionInfo.versionInfo.version,
                    "",
                    Contact("Henrik Binggl", "https://github.com/bihe/mydms-java", ""),
                    "MIT", "https://raw.githubusercontent.com/bihe/mydms-java/master/LICENSE", Collections.emptyList()
            )
    }

}


