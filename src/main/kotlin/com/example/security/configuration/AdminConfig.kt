package com.example.security.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

@ConstructorBinding
@ConfigurationProperties("app.security.admin")
data class AdminConfig(
    val username: String,
    val password: String,
    val roles: Set<String>,
) {

    val adminUser: User
        get() = User(
            username,
            password,
            roles.map { SimpleGrantedAuthority(it) }
        )
}
