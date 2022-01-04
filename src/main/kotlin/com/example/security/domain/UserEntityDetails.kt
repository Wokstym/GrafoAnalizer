package com.example.security.domain

import com.example.user.domain.UserEntity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserEntityDetails(
    private val entity: UserEntity
) : UserDetails {

    override fun getAuthorities() = entity.roles.map { SimpleGrantedAuthority(it) }

    override fun getPassword() = entity.password

    override fun getUsername() = entity.username

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true
}
