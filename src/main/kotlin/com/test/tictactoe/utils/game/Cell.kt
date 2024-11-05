package com.test.tictactoe.utils.game

import com.test.tictactoe.enum.GameSymbol

data class Cell(
    val x: Int,
    val y: Int,
    val symbol: GameSymbol?,
)