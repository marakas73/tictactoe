package com.test.tictactoe.utils.ai

import com.test.tictactoe.utils.game.Move

data class ScoredMove(
    val move: Move,
    val score: Int,
)