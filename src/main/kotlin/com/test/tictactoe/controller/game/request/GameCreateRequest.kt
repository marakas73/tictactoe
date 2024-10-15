package com.test.tictactoe.controller.game.request

import com.test.tictactoe.enum.GameSymbol

data class GameCreateRequest(
    val ownerSymbol: GameSymbol,
    val memberSymbol: GameSymbol,
    val width: Int,
    val height: Int,
    val needToWin: Int
)