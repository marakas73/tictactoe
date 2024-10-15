package com.test.tictactoe.controller.user.response

data class UserInfoResponse(
    val id: Long,
    val login: String,
    var rating: Int,
    var currentGameId: Long?,
)