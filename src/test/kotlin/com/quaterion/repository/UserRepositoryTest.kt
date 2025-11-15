package com.quaterion.repository

import com.quaterion.entity.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should find user by username`() {
        val user = User(username = "testuser", password = "password123", role = "USER")
        entityManager.persist(user)
        entityManager.flush()

        val found = userRepository.findByUsername("testuser")

        assertNotNull(found)
        assertEquals("testuser", found?.username)
        assertEquals("password123", found?.password)
        assertEquals("USER", found?.role)
    }

    @Test
    fun `should return null when user not found by username`() {
        val found = userRepository.findByUsername("nonexistent")

        assertNull(found)
    }

    @Test
    fun `should return true when user exists by username`() {
        val user = User(username = "testuser", password = "password123", role = "USER")
        entityManager.persist(user)
        entityManager.flush()

        val exists = userRepository.existsByUsername("testuser")

        assertTrue(exists)
    }

    @Test
    fun `should return false when user does not exist by username`() {
        val exists = userRepository.existsByUsername("nonexistent")

        assertFalse(exists)
    }

    @Test
    fun `should save user successfully`() {
        val user = User(username = "newuser", password = "password123", role = "ADMIN")

        val saved = userRepository.save(user)

        assertNotNull(saved.id)
        assertEquals("newuser", saved.username)
        assertEquals("password123", saved.password)
        assertEquals("ADMIN", saved.role)
    }

    @Test
    fun `should enforce unique username constraint`() {
        val user1 = User(username = "duplicate", password = "password1", role = "USER")
        entityManager.persist(user1)
        entityManager.flush()

        val user2 = User(username = "duplicate", password = "password2", role = "USER")

        assertThrows(Exception::class.java) {
            userRepository.save(user2)
            entityManager.flush()
        }
    }

    @Test
    fun `should find all users`() {
        val user1 = User(username = "user1", password = "password1", role = "USER")
        val user2 = User(username = "user2", password = "password2", role = "ADMIN")
        entityManager.persist(user1)
        entityManager.persist(user2)
        entityManager.flush()

        val users = userRepository.findAll()

        assertEquals(2, users.size)
        assertTrue(users.any { it.username == "user1" })
        assertTrue(users.any { it.username == "user2" })
    }

    @Test
    fun `should delete user by id`() {
        val user = User(username = "deleteuser", password = "password123", role = "USER")
        entityManager.persist(user)
        entityManager.flush()
        val userId = user.id!!

        userRepository.deleteById(userId)

        val found = userRepository.findById(userId)
        assertFalse(found.isPresent)
    }
}
