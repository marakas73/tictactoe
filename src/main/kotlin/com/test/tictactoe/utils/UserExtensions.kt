package com.test.tictactoe.utils

import com.test.tictactoe.controller.auth.request.RegisterRequest
import com.test.tictactoe.controller.auth.response.RegisterResponse
import com.test.tictactoe.controller.user.response.UserInfoResponse
import com.test.tictactoe.model.User

fun RegisterRequest.toModel(): User =
    User(
        login = this.login,
        password = this.password
    )

fun User.toResponse(): RegisterResponse =
    RegisterResponse(
        id = this.id,
        login = this.login
    )

fun User.toUserInfoResponse(): UserInfoResponse =
    UserInfoResponse(
        id = this.id,
        login = this.login,
        rating = this.rating,
        currentGameId = this.currentGame?.id,
    )