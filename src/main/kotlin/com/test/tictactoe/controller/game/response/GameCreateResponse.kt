package com.test.tictactoe.controller.game.response

import com.test.tictactoe.enum.GameSymbol

data class GameCreateResponse(
    val id: Long,
    val ownerSymbol: GameSymbol,
    val memberSymbol: GameSymbol
)