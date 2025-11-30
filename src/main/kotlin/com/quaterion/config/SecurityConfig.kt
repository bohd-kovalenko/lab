package com.quaterion.config

import com.quaterion.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * Spring Security configuration for JWT-based authentication.
 *
 * Configures stateless session management, JWT authentication filter,
 * and defines public/protected endpoints.
 *
 * @property jwtAuthenticationFilter filter for processing JWT tokens in requests
 * @property userDetailsService service for loading user details during authentication
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val userDetailsService: UserDetailsService
) {

    /**
     * Creates a BCrypt password encoder bean.
     *
     * @return [PasswordEncoder] using BCrypt hashing algorithm
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    /**
     * Creates the authentication provider using DAO authentication.
     *
     * @return [AuthenticationProvider] configured with user details service and password encoder
     */
    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    /**
     * Exposes the authentication manager as a bean.
     *
     * @param config Spring's authentication configuration
     * @return [AuthenticationManager] for authenticating user credentials
     */
    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    /**
     * Configures the security filter chain.
     *
     * Security rules:
     * - CSRF disabled (stateless JWT authentication)
     * - Public endpoints: `/auth/**`, `/h2-console/**`
     * - All other endpoints require authentication
     * - Stateless session management
     * - JWT filter added before username/password authentication
     *
     * @param http HttpSecurity builder
     * @return configured [SecurityFilterChain]
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/auth/**", "/h2-console/**").permitAll()
                    .anyRequest().authenticated()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )

        http.headers { headers -> headers.frameOptions { it.disable() } }

        return http.build()
    }
}
