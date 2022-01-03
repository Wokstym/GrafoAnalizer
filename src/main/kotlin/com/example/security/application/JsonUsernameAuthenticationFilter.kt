package com.example.security.application

import com.example.security.domain.LoginRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JsonUsernameAuthenticationFilter : UsernamePasswordAuthenticationFilter() {
    private val mapper = ObjectMapper()

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val loginRequest = mapper.readValue(request.reader, LoginRequest::class.java)
        val token = UsernamePasswordAuthenticationToken(
            loginRequest.username, loginRequest.password
        )
        return authenticationManager.authenticate(token)
    }
}
