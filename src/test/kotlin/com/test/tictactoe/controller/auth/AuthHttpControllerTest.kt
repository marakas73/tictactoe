package com.test.tictactoe.controller.auth

import com.test.tictactoe.controller.auth.request.AuthenticationRequest
import com.test.tictactoe.controller.auth.request.RegisterRequest
import com.test.tictactoe.controller.auth.response.AuthenticationResponse
import com.test.tictactoe.controller.auth.response.RegisterResponse
import com.test.tictactoe.model.User
import com.test.tictactoe.service.AuthenticationService
import com.test.tictactoe.service.UserService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.server.ResponseStatusException

class AuthHttpControllerTest {

    private lateinit var authenticationService: AuthenticationService
    private lateinit var userService: UserService
    private lateinit var controller: AuthHttpController

    @BeforeEach
    fun setup() {
        authenticationService = mockk()
        userService = mockk()
        controller = AuthHttpController(authenticationService, userService)
    }

    @Test
    fun `register should return RegisterResponse when user is created successfully`() {
        // Arrange
        val request = RegisterRequest("testuser", "password123")
        val user = User(1, "testuser", "password123")
        coEvery { userService.createUser(any()) } returns user

        // Act
        val result = runBlocking {
            controller.register(request)
        }

        // Assert
        assertEquals(RegisterResponse(1, "testuser"), result)
    }

    @Test
    fun `register should throw ResponseStatusException when user creation fails`() {
        // Arrange
        val request = RegisterRequest("testuser", "password123")
        coEvery { userService.createUser(any()) } returns null

        // Act & Assert
        assertThrows<ResponseStatusException> {
            runBlocking { controller.register(request) }
        }
    }
}