package com.test.tictactoe.utils.ai

import com.test.tictactoe.enum.GameSymbol
import com.test.tictactoe.model.Field
import com.test.tictactoe.model.Game
import com.test.tictactoe.utils.game.*

object Evaluator {
    private val SCORES = intArrayOf(19, 15, 11, 7, 3)

    private fun scoreDirection(direction: Direction, symbol: GameSymbol): Int {
        var score = 0

        // Pass a window of 5 across the field array
        var i = 0
        while ((i + 4) < direction.sequence.size) {
            var empty = 0
            var stones = 0
            for (j in 0..4) {
                if (direction.sequence[i + j].symbol == null) {
                    empty++
                } else if (direction.sequence[i + j].symbol == symbol) {
                    stones++
                } else {
                    // Opponent stone in this window, can't form a five
                    break
                }
            }
            // Ignore already formed fives, and empty windows
            if (empty == 0 || empty == 5) {
                i++
                continue
            }

            // Window contains only empty spaces and player stones, can form
            // a five, get score based on how many moves needed
            if (stones + empty == 5) {
                score += SCORES[empty]
            }
            i++
        }

        return score
    }

    fun evaluateState(game: Game, lastMove: Move, depth: Int): Int {
        val playerSymbol = game.getNonCurrentMoveSymbol()
        val opponentSymbol = game.currentMove

        // Check for a winning/losing position
        val winner = getWinner(game, lastMove)
        if (winner == playerSymbol) return 10000 + depth
        if (winner == opponentSymbol) return -10000 - depth

        // Evaluate each field separately, subtracting from the score if the
        // field belongs to the opponent, adding if it belongs to the player
        var score = 0
        for (y in 0 until game.field.height) {
            for (x in 0 until game.field.width) {
                if (game.field.field[y][x] == opponentSymbol) {
                    score -= evaluateCell(game.field, x, y, opponentSymbol)
                } else if (game.field.field[y][x] == playerSymbol) {
                    score += evaluateCell(game.field, x, y, playerSymbol)
                }
            }
        }

        return score
    }

    fun evaluateCell(field: Field, x: Int, y: Int, symbol: GameSymbol): Int {
        var score = 0

        for (direction in field.getAllDirections(x, y)) {
            score += scoreDirection(direction, symbol)
        }

        return score
    }
}