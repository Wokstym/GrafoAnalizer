package com.example.security.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.web.client.RestTemplate

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
class SecurityConfiguration(
    var repository: ClientRegistrationRepository
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests()
            .mvcMatchers(HttpMethod.POST, "/login", "/oauth2/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .httpBasic()
            .and()
            .csrf { it.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) }
            .oauth2Login()
            .authorizationEndpoint()
            .authorizationRequestResolver(CustomAuthorizationRequestResolver(repository, "/oauth2/authorization"))
            .and()
            .tokenEndpoint()
            .accessTokenResponseClient(accessTokenResponseClient())
    }

    @Bean
    fun accessTokenResponseClient(): OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
        val tokenResponseHttpMessageConverter = OAuth2AccessTokenResponseHttpMessageConverter()
        val restTemplate = RestTemplate(listOf(FormHttpMessageConverter(), tokenResponseHttpMessageConverter))
        restTemplate.errorHandler = OAuth2ErrorResponseErrorHandler()

        return DefaultAuthorizationCodeTokenResponseClient().apply {
            setRequestEntityConverter(CustomRequestEntityConverter())
            setRestOperations(restTemplate)
        }
    }
}
