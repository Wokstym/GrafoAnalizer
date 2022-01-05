package com.example.security.application

import com.example.user.db.UserRepository
import com.example.user.domain.UserEntity
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class GrafioAnalizerOAuth2UserService(
    val userRepository: UserRepository
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    val default: DefaultOAuth2UserService = DefaultOAuth2UserService()

    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {
        val user = default.loadUser(userRequest)
        val data = user.getAttribute<LinkedHashMap<String, String>>("data") ?: throw Exception(";(")
        val id = data["id"]!!
        var userEntity = userRepository.findByTwitterId(id)
        if (userEntity == null) {
            val name = data["name"]!!
            val username = data["username"]!!
            val newUser = UserEntity(username = username, twitterId = id, name = name)
            userEntity = userRepository.save(newUser)
        }


        return user
    }
}



//class OAuth2UserEntityDetails(
//    fromEntity: UserEntity? = null,
//    private val fromOAuth2User: OAuth2User? = null
//) : UserEntity(), OAuth2User {
//
//
//    init {
//        fromEntity?.also { from ->
//            id = from.id
//            roles = from.roles
//            createdAt = from.createdAt
//            updatedAt = from.updatedAt
//        }
//
//    }
//
//
//    override fun getName(): String? = fromOAuth2User?.name
//
//    override fun getAttributes(): MutableMap<String, Any>? = fromOAuth2User?.attributes
//
//    override fun getAuthorities(): MutableCollection<out GrantedAuthority>? = fromOAuth2User?.authorities
//
//}