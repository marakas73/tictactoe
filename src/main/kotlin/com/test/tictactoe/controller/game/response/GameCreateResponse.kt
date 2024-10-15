package com.test.tictactoe.controller.game.response

import com.test.tictactoe.enum.GameSymbol

data class GameCreateResponse(
    val id: Long,
    val width: Int,
    val height: Int,
    val needToWin: Int,
    val ownerSymbol: GameSymbol,
    val memberSymbol: GameSymbol
)