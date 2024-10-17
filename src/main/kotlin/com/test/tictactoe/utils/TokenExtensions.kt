package com.test.tictactoe.utils

import com.test.tictactoe.controller.auth.response.TokenResponse

fun String.mapToTokenResponse(): TokenResponse =
    TokenResponse(
        token = this
    )