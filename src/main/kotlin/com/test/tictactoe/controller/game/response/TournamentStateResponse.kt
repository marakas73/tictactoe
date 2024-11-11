package com.test.tictactoe.controller.game.response

import com.test.tictactoe.utils.game.SimpleRoundGame

data class TournamentStateResponse(
    val ownerLogin: String,
    val playersCount: Int,
    val playersLogin: List<String>,
    val roundGames: Map<Int, List<SimpleRoundGame?>>,
    val isStarted: Boolean,
)
