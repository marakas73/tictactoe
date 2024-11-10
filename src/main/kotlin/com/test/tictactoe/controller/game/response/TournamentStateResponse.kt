package com.test.tictactoe.controller.game.response

import com.test.tictactoe.model.RoundGame

data class TournamentStateResponse(
    val ownerLogin: String,
    val playersCount: Int,
    val playerLogins: List<String>,
    val roundGames: List<RoundGame>,
    val isStarted: Boolean,
)
