package com.test.tictactoe.controller.game.response

import com.test.tictactoe.enum.GameStatus
import com.test.tictactoe.enum.GameSymbol

data class GameStateResponse (
    val field: List<MutableList<GameSymbol?>>,
    val currentMove: GameSymbol,
    val gameStatus: GameStatus,
    val ownerLogin: String,
    val memberLogin: String?,
    val ownerSymbol: GameSymbol,
    val memberSymbol: GameSymbol,
)