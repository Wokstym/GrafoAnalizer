package com.example.user.contoller

import com.example.security.application.OAuth2UserEntityDetails
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RequestMapping("/users")
@RestController
class UserController {

    @GetMapping("/me")
    fun generateFromList(
        @AuthenticationPrincipal user: OAuth2UserEntityDetails
    ): ResponseEntity<UserBasicDto> {
        return ResponseEntity.ok(UserBasicDto(user.db.username, user.db.nameAndSurname, user.db.createdAt))
    }
}

data class UserBasicDto(
    val username: String,
    val nameAndSurname: String,
    val createdAt: LocalDateTime
)