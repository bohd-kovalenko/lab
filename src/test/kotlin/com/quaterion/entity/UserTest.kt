package com.quaterion.entity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    fun `should create user with all properties`() {
        val username = "testuser"
        val password = "password123"
        val role = "ADMIN"
        val id = 1L

        val user = User(
            id = id,
            username = username,
            password = password,
            role = role
        )

        assertEquals(id, user.id)
        assertEquals(username, user.username)
        assertEquals(password, user.password)
        assertEquals(role, user.role)
    }

    @Test
    fun `should create user with default role`() {
        val user = User(
            username = "testuser",
            password = "password123"
        )

        assertEquals("USER", user.role)
    }

    @Test
    fun `should create user without id`() {
        val user = User(
            username = "testuser",
            password = "password123",
            role = "USER"
        )

        assertNull(user.id)
    }

    @Test
    fun `should support data class equality`() {
        val user1 = User(id = 1L, username = "test", password = "pass", role = "USER")
        val user2 = User(id = 1L, username = "test", password = "pass", role = "USER")
        val user3 = User(id = 2L, username = "test", password = "pass", role = "USER")

        assertEquals(user1, user2)
        assertNotEquals(user1, user3)
    }

    @Test
    fun `should support data class copy`() {
        val user = User(id = 1L, username = "test", password = "pass", role = "USER")

        val copiedUser = user.copy(username = "newtest")

        assertEquals(1L, copiedUser.id)
        assertEquals("newtest", copiedUser.username)
        assertEquals("pass", copiedUser.password)
        assertEquals("USER", copiedUser.role)
    }
}
