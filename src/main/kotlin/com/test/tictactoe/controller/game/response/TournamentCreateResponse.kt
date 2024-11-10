package com.test.tictactoe.controller.game.response

import com.test.tictactoe.enum.GameSymbol
import java.util.*

data class TournamentCreateResponse(
    val id: Long,
    val playersCount: Int
)