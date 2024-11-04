package com.test.tictactoe.utils.game

data class Move(
    val x: Int,
    val y: Int,
) {
    override fun toString(): String {
        return "($x-$y)"
    }
}