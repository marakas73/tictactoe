package com.test.tictactoe.controller.game.request

import com.test.tictactoe.enum.GameSymbol

data class TournamentCreateRequest(
    val playersCount: Int
)