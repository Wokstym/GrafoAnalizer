package com.example.user.service

import com.example.user.db.UserRepository
import com.example.user.domain.UserEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserRegistrationService(
    private val repository: UserRepository,
    private val encoder: PasswordEncoder,
) {

    fun register(username: String, password: String) {
        require(repository.findByUsernameIgnoreCase(username) == null) { "User already exists" }

        val entity = UserEntity(username, encoder.encode(password))
        repository.save(entity)
    }
}
