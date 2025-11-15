package com.quaterion.config

import com.quaterion.security.JwtAuthenticationFilter
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter

    @Test
    fun `should allow access to auth endpoints without authentication`() {
        mockMvc.perform(
            post("/auth/login")
                .contentType("application/json")
                .content("""{"username":"testuser","password":"password123"}""")
        )
            .andExpect(status().isUnauthorized) // Unauthorized because user doesn't exist, but endpoint is accessible
    }

    @Test
    fun `should deny access to protected endpoints without authentication`() {
        mockMvc.perform(get("/api/hello"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `should have password encoder bean configured`() {
        val password = "testPassword123"

        val encodedPassword = passwordEncoder.encode(password)

        assert(passwordEncoder.matches(password, encodedPassword))
        assert(!passwordEncoder.matches("wrongPassword", encodedPassword))
    }

    @Test
    fun `should use BCrypt password encoder`() {
        val password = "testPassword"

        val encoded = passwordEncoder.encode(password)

        assert(encoded.startsWith("\$2a\$") || encoded.startsWith("\$2b\$") || encoded.startsWith("\$2y\$"))
    }

    @Test
    fun `should have JWT authentication filter bean configured`() {
        assert(jwtAuthenticationFilter != null)
    }

    @Test
    fun `should allow access to H2 console`() {
        mockMvc.perform(get("/h2-console"))
            .andExpect(status().isNotFound) // 404 because H2 console page doesn't exist at root, but it's permitted
    }

    @Test
    fun `password encoder should generate different hashes for same password`() {
        val password = "samePassword"

        val hash1 = passwordEncoder.encode(password)
        val hash2 = passwordEncoder.encode(password)

        assert(hash1 != hash2) // BCrypt uses random salt
        assert(passwordEncoder.matches(password, hash1))
        assert(passwordEncoder.matches(password, hash2))
    }
}
