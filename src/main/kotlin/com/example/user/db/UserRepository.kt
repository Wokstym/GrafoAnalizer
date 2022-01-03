package com.example.user.db

import com.example.user.domain.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long> {

    fun findByUsernameIgnoreCase(username: String): UserEntity?
}
