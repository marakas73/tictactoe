package com.test.tictactoe.service

import com.test.tictactoe.model.User
import com.test.tictactoe.repository.GameHistoryRepository
import com.test.tictactoe.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertEquals

class UserServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var gameHistoryRepository: GameHistoryRepository
    private lateinit var encoder: PasswordEncoder
    private lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        userRepository = mockk()
        gameHistoryRepository = mockk()
        encoder = mockk()
        userService = UserService(userRepository, encoder, gameHistoryRepository)
    }

    @Test
    fun `create user should return User when user is created successfully`() {
        // Arrange
        every { userRepository.existsByLogin("test") } returns false
        every { encoder.encode("test") } returns "test"

        val user = User(
            id = 0,
            login = "test",
            password = "test"
        )

        every { userRepository.save(user) } returns user

        // Act
        val result = runBlocking {
            userService.createUser(user)
        }

        // Assert
        assertEquals(user, result)
    }

}
