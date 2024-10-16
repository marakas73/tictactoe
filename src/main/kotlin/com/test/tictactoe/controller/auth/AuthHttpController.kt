package com.test.tictactoe.controller.auth

import com.test.tictactoe.controller.auth.request.AuthenticationRequest
import com.test.tictactoe.controller.auth.request.RefreshTokenRequest
import com.test.tictactoe.controller.auth.request.RegisterRequest
import com.test.tictactoe.controller.auth.response.AuthenticationResponse
import com.test.tictactoe.controller.auth.response.RegisterResponse
import com.test.tictactoe.controller.auth.response.TokenResponse
import com.test.tictactoe.model.User
import com.test.tictactoe.service.AuthenticationService
import com.test.tictactoe.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/auth")
class AuthHttpController(
    private val authenticationService: AuthenticationService,
    private val userService: UserService
) {
    @PostMapping("/register")
    suspend fun register(@RequestBody userRequest: RegisterRequest): RegisterResponse =
        userService.createUser(
            userRequest.toModel()
        )
            ?.toResponse()
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create a user.")

    private fun RegisterRequest.toModel(): User =
        User(
            login = this.login,
            password = this.password
        )

    private fun User.toResponse(): RegisterResponse =
        RegisterResponse(
            id = this.id,
            login = this.login
        )
    @PostMapping("/login")
    suspend fun authenticate(
        @RequestBody authRequest: AuthenticationRequest
    ): AuthenticationResponse {
        return authenticationService.authentication(authRequest)
    }
    @PostMapping("/refresh")
    suspend fun refreshAccessToken(
        @RequestBody request: RefreshTokenRequest
    ): TokenResponse =
        authenticationService.refreshAccessToken(request.token)
            ?.mapToTokenResponse()
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token")

    private fun String.mapToTokenResponse(): TokenResponse =
        TokenResponse(
            token = this
        )
}