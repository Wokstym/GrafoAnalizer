package com.example.security.application

import com.example.security.configuration.AdminConfig
import com.example.security.domain.UserEntityDetails
import com.example.user.db.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class GrafioAnalizerUserDetailsService(
    private val repository: UserRepository,
    private val config: AdminConfig
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        return if (config.username.equals(username, ignoreCase = true)) {
            config.adminUser
        } else {
            val user = repository.findByUsernameIgnoreCase(username) ?: throw UsernameNotFoundException(username)
            UserEntityDetails(user)
        }
    }
}
