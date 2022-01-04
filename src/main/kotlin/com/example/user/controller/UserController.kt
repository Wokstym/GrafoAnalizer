package com.example.user.controller

import com.example.user.service.UserRegistrationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    val service: UserRegistrationService
) {
    @PostMapping
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<Nothing> {
        service.register(request.username, request.password)
        return ResponseEntity.accepted().build()
    }

    data class RegisterRequest(
        var password: String,
        var username: String
    )
}
