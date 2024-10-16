package com.test.tictactoe.service

import com.test.tictactoe.config.JwtProperties
import com.test.tictactoe.controller.auth.request.AuthenticationRequest
import com.test.tictactoe.controller.auth.response.AuthenticationResponse
import com.test.tictactoe.repository.RefreshTokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    suspend fun authentication(
        authRequest: AuthenticationRequest
    ): AuthenticationResponse = withContext(Dispatchers.IO) {
        authManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authRequest.login,
                authRequest.password
            )
        )

        val user = userDetailsService.loadUserByUsername(authRequest.login)

        val accessToken = generateAccessToken(user)
        val refreshToken = generateRefreshToken(user)

        refreshTokenRepository.save(refreshToken, user)

        AuthenticationResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    suspend fun refreshAccessToken(
        token: String
    ): String? = withContext(Dispatchers.IO){
        val extractedLogin = tokenService.extractLogin(token)

         extractedLogin?.let { login ->
            val  currentUserDetails = userDetailsService.loadUserByUsername(login)
            val refreshTokenUserDetails = refreshTokenRepository.findUserDetailsByToken(token)

            if(!tokenService.isExpired(token) && currentUserDetails.username == refreshTokenUserDetails?.username)
                generateAccessToken(currentUserDetails)
            else
                null
        }
    }

    private fun generateRefreshToken(user: UserDetails) = tokenService.generate(
        userDetails = user,
        expirationDate = Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration)
    )

    private fun generateAccessToken(user: UserDetails) = tokenService.generate(
        userDetails = user,
        expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
    )

}
