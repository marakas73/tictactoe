package com.test.tictactoe.utils.game

import com.test.tictactoe.enum.GameSymbol

data class Cell(
    val x: Int,
    val y: Int,
    val symbol: GameSymbol?,
) {
    override fun toString(): String { // TODO
        return "($x-$y $symbol)"
    }
}