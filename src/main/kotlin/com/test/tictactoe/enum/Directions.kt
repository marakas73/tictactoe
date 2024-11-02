package com.test.tictactoe.enum

enum class Directions (
    val fromDeltaX: Int,
    val fromDeltaY: Int,
    val toDeltaX: Int,
    val toDeltaY: Int
) {
    VERTICAL(0, -1, 0, 1),
    HORIZONTAL(1, 0, -1, 0),
    MAIN_DIAGONAL(-1, -1, 1, 1),
    SECONDARY_DIAGONAL(1, -1, -1, 1),
}