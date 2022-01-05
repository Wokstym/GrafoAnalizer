package com.example.security.application

import com.example.user.db.UserRepository
import com.example.user.domain.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class TwitterOAuth2UserService(
    val userRepository: UserRepository
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    val default: DefaultOAuth2UserService = DefaultOAuth2UserService()

    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {
        val user = default.loadUser(userRequest)

        val (data, id) = user.extractAttributes()

        var userEntity = userRepository.findByTwitterId(id)
        if (userEntity == null) {
            val (name, username) = extractNames(data)
            val newUser = UserEntity(username = username, twitterId = id, name = name)
            userEntity = userRepository.save(newUser)
        }

        return OAuth2UserEntityDetails(userEntity, user.authorities, data)
    }

    private fun OAuth2User.extractAttributes(): Pair<LinkedHashMap<String, String>, String> {
        val data = getAttribute<LinkedHashMap<String, String>>("data") ?: throw error("data")
        val id = data["id"] ?: throw error("id")
        return Pair(data, id)
    }

    private fun extractNames(data: LinkedHashMap<String, String>): Pair<String, String> {
        val name = data["name"] ?: throw error("name")
        val username = data["username"] ?: throw error("username")
        return Pair(name, username)
    }

    fun error(attributeName: String) =
        OAuth2AuthenticationException("Error loading user by Twitter authentication - no $attributeName attribute")
}

class OAuth2UserEntityDetails(
    val db: UserEntity,
    private val authorities: MutableCollection<out GrantedAuthority>,
    private val data: MutableMap<String, String>
) : OAuth2User {

    override fun getName(): String = db.nameAndSurname

    override fun getAttributes(): MutableMap<String, String> = data

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities
}
