package com.test.tictactoe.controller.game.response

data class TournamentStateResponse(
    val ownerLogin: String,
    val playersCount: Int,
    val playersLogin: List<String>,
    val roundWinnersLogin: Map<Int, List<String?>>,
    val isStarted: Boolean,
)
