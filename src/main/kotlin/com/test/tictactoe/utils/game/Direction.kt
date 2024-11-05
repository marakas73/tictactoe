package com.test.tictactoe.utils.game

data class Direction(
    val sequence: List<Cell>,
    val centerIndex: Int,
) {
    override fun toString(): String {
        return "(${sequence.map { it.symbol }}, cI=$centerIndex)"
    }
}
