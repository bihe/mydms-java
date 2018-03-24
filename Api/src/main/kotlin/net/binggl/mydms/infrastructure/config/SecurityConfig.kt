package net.binggl.mydms.infrastructure.config

import net.binggl.mydms.infrastructure.security.JwtAuthenticator
import net.binggl.mydms.infrastructure.security.JwtAuthorizationFilter
import net.binggl.mydms.infrastructure.security.RoleAuthorizer
import net.binggl.mydms.infrastructure.security.SecurityAuthenticationEntryPoint
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy


@Configuration
@EnableWebSecurity
class SecurityConfig(@Autowired private val jwtAuthenticator: JwtAuthenticator,
                     @Autowired private val roleAuthorizer: RoleAuthorizer,
                     @Autowired private val authEntryPoint: SecurityAuthenticationEntryPoint,
                     @Value("\${auth.cookieName}") private val cookieName: String) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {

        http.exceptionHandling().authenticationEntryPoint(authEntryPoint)

        http.cors().and().csrf().disable().authorizeRequests()

                .antMatchers("/api/v1/appinfo").hasAuthority("User")
                .antMatchers("/api/v1/file").hasAuthority("User")
                .antMatchers("/api/v1/documents").hasAuthority("User")
                .antMatchers("/api/v1/senders").hasAuthority("User")
                .antMatchers("/api/v1/tags").hasAuthority("User")
                .antMatchers("/api/v1/upload/file").hasAuthority("User")
                .antMatchers("/").hasAuthority("User")

                    .anyRequest().authenticated()

                //.antMatchers("/swagger-resources", "/swagger-resources/**", "/swagger-resources/**/**").permitAll()

                .and()
                .addFilter(JwtAuthorizationFilter(authenticationManager(), roleAuthorizer, jwtAuthenticator, cookieName))
                // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

    }

    override fun configure(web: WebSecurity) {
        web.ignoring()
                .antMatchers("/login", "/login/**")
                .antMatchers("/assets/**/**")
                .antMatchers("/favicon.ico")
                .antMatchers("/swagger-resources", "/swagger-resources/**", "/swagger-resources/**/**")
    }
}