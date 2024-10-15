package com.test.tictactoe.controller.auth.response

data class AuthenticationResponse(
    val accessToken: String,
    val refreshToken: String
) {

}
