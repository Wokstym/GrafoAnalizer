package com.example.security.configuration

import com.example.security.application.GrafioAnalizerUserDetailsService
import com.example.security.application.JsonUsernameAuthenticationFilter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableConfigurationProperties(AdminConfig::class)
class SecurityConfiguration(
    var service: GrafioAnalizerUserDetailsService
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        http
            .authorizeRequests()
            .mvcMatchers(HttpMethod.POST, "/login", "/users").permitAll()
            .anyRequest().authenticated()
            .and()
            .httpBasic()
            .and()
            .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }

    private fun authenticationFilter(): JsonUsernameAuthenticationFilter {
        return JsonUsernameAuthenticationFilter().apply {
            setAuthenticationManager(super.authenticationManager())
        }
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(authenticationProvider())
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider? {
        return DaoAuthenticationProvider().apply {
            setUserDetailsService(service)
            setPasswordEncoder(passwordEncoder())
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder? {
        return BCryptPasswordEncoder()
    }
}
