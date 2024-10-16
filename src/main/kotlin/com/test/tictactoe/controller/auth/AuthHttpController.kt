package com.test.tictactoe.controller.auth

import com.test.tictactoe.controller.auth.request.AuthenticationRequest
import com.test.tictactoe.controller.auth.request.RefreshTokenRequest
import com.test.tictactoe.controller.auth.request.RegisterRequest
import com.test.tictactoe.controller.auth.response.AuthenticationResponse
import com.test.tictactoe.controller.auth.response.RegisterResponse
import com.test.tictactoe.controller.auth.response.TokenResponse
import com.test.tictactoe.exception.throwCannotCreateUserException
import com.test.tictactoe.exception.throwInvalidRefreshTokenException
import com.test.tictactoe.service.AuthenticationService
import com.test.tictactoe.service.UserService
import com.test.tictactoe.utils.mapToTokenResponse
import com.test.tictactoe.utils.toModel
import com.test.tictactoe.utils.toResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
            ?: throwCannotCreateUserException()

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
            ?: throwInvalidRefreshTokenException()
}