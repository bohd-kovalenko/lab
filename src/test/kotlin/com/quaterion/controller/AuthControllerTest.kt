package com.quaterion.controller

import com.quaterion.dto.AuthRequest
import com.quaterion.entity.User
import com.quaterion.repository.UserRepository
import com.quaterion.security.JwtUtil
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder

class AuthControllerTest {

    private lateinit var authenticationManager: AuthenticationManager
    private lateinit var userRepository: UserRepository
    private lateinit var jwtUtil: JwtUtil
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var authController: AuthController

    @BeforeEach
    fun setUp() {
        authenticationManager = mockk()
        userRepository = mockk()
        jwtUtil = mockk()
        passwordEncoder = mockk()
        authController = AuthController(authenticationManager, userRepository, jwtUtil, passwordEncoder)
    }

    @Test
    fun `should register new user successfully`() {
        val request = AuthRequest("newuser", "password123")
        val encodedPassword = "encodedPassword"
        val savedUser = User(id = 1L, username = "newuser", password = encodedPassword, role = "USER")
        val token = "generated.jwt.token"

        every { userRepository.existsByUsername("newuser") } returns false
        every { passwordEncoder.encode("password123") } returns encodedPassword
        every { userRepository.save(any()) } returns savedUser
        every { jwtUtil.generateToken("newuser") } returns token

        val response = authController.register(request)

        assert(response.statusCode == HttpStatus.CREATED)
        verify { userRepository.existsByUsername("newuser") }
        verify { passwordEncoder.encode("password123") }
        verify { userRepository.save(any()) }
        verify { jwtUtil.generateToken("newuser") }
    }

    @Test
    fun `should return conflict when username already exists`() {
        val request = AuthRequest("existinguser", "password123")
        every { userRepository.existsByUsername("existinguser") } returns true

        val response = authController.register(request)

        assert(response.statusCode == HttpStatus.CONFLICT)
        verify { userRepository.existsByUsername("existinguser") }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should login user successfully`() {
        val request = AuthRequest("testuser", "password123")
        val token = "generated.jwt.token"
        val authToken = UsernamePasswordAuthenticationToken("testuser", "password123")

        every { authenticationManager.authenticate(any()) } returns authToken
        every { jwtUtil.generateToken("testuser") } returns token

        val response = authController.login(request)

        assert(response.statusCode == HttpStatus.OK)
        verify { authenticationManager.authenticate(any()) }
        verify { jwtUtil.generateToken("testuser") }
    }

    @Test
    fun `should return unauthorized when login credentials are invalid`() {
        val request = AuthRequest("testuser", "wrongpassword")

        every { authenticationManager.authenticate(any()) } throws BadCredentialsException("Bad credentials")

        val response = authController.login(request)

        assert(response.statusCode == HttpStatus.UNAUTHORIZED)
        verify { authenticationManager.authenticate(any()) }
        verify(exactly = 0) { jwtUtil.generateToken(any()) }
    }

    @Test
    fun `should save user with USER role during registration`() {
        val request = AuthRequest("newuser", "password123")
        val encodedPassword = "encodedPassword"
        val token = "generated.jwt.token"
        var capturedUser: User? = null

        every { userRepository.existsByUsername("newuser") } returns false
        every { passwordEncoder.encode("password123") } returns encodedPassword
        every { userRepository.save(any()) } answers {
            capturedUser = firstArg()
            capturedUser!!.copy(id = 1L)
        }
        every { jwtUtil.generateToken("newuser") } returns token

        authController.register(request)

        assert(capturedUser?.role == "USER")
        assert(capturedUser?.username == "newuser")
        assert(capturedUser?.password == encodedPassword)
    }

    @Test
    fun `should encode password before saving during registration`() {
        val request = AuthRequest("newuser", "plainPassword")
        val encodedPassword = "\$2a\$10\$encodedHash"
        val token = "generated.jwt.token"

        every { userRepository.existsByUsername("newuser") } returns false
        every { passwordEncoder.encode("plainPassword") } returns encodedPassword
        every { userRepository.save(any()) } returns User(id = 1L, username = "newuser", password = encodedPassword, role = "USER")
        every { jwtUtil.generateToken("newuser") } returns token

        authController.register(request)

        verify { passwordEncoder.encode("plainPassword") }
    }

    @Test
    fun `should generate JWT token after successful login`() {
        val request = AuthRequest("testuser", "password123")
        val token = "jwt.token.here"
        val authToken = UsernamePasswordAuthenticationToken("testuser", "password123")

        every { authenticationManager.authenticate(any()) } returns authToken
        every { jwtUtil.generateToken("testuser") } returns token

        authController.login(request)

        verify { jwtUtil.generateToken("testuser") }
    }

    @Test
    fun `should generate JWT token after successful registration`() {
        val request = AuthRequest("newuser", "password123")
        val encodedPassword = "encodedPassword"
        val token = "jwt.token.here"

        every { userRepository.existsByUsername("newuser") } returns false
        every { passwordEncoder.encode("password123") } returns encodedPassword
        every { userRepository.save(any()) } returns User(id = 1L, username = "newuser", password = encodedPassword, role = "USER")
        every { jwtUtil.generateToken("newuser") } returns token

        authController.register(request)

        verify { jwtUtil.generateToken("newuser") }
    }
}
