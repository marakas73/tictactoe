package com.test.tictactoe.controller.auth.request

data class AuthenticationRequest(
    val login: String,
    val password: String
)