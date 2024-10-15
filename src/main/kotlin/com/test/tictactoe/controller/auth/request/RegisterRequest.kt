package com.test.tictactoe.controller.auth.request

data class RegisterRequest (
    val login: String,
    val password: String,
)