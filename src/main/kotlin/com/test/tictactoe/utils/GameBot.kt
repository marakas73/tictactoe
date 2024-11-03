package com.test.tictactoe.utils

import com.test.tictactoe.enum.GameSymbol
import com.test.tictactoe.model.Field
import com.test.tictactoe.model.Game
import kotlin.math.max
import kotlin.math.min

object GameBot {
    private const val START_DEPTH = 2
    private const val END_DEPTH = 8

    fun getBestMove(game: Game): Move {

        // Run a depth increasing search
        val bestMove = iterativeDeepening(game, START_DEPTH, END_DEPTH)
        return bestMove
    }

    private fun iterativeDeepening(game: Game, startDepth: Int, endDepth: Int): Move {
        var moves = getSortedPotentialMoves(game)
        if (moves.size == 1) return moves[0]
        for (i in startDepth..endDepth) {
            try {
                moves = getScoredMoves(game, moves, i)
            } catch (e: InterruptedException) {
                break
            }
        }
        return moves[0]
    }

    private fun getScoredMoves(game: Game, moves: List<Move>, depth: Int): List<Move> {
        val scoredMoves = mutableListOf<ScoredMove>()
        var alpha = -11000
        val beta = 11000
        var best = Int.MIN_VALUE

        for (move in moves) {
            game.makeMove(move)

            val score = -negamax(
                game,
                move,
                depth - 1,
                -beta,
                -alpha
            )
            scoredMoves.add(
                ScoredMove(
                    move = move,
                    score = score
                )
            )

            game.undoMove(move)
            if (score > best) best = score
            if (best > alpha) alpha = best
            if (best >= beta) break
        }

        scoredMoves.sortByDescending { it.score }

        return scoredMoves.map { it.move }
    }

    private fun negamax(
        game: Game,
        move: Move,
        depth: Int,
        alpha: Int,
        beta: Int
    ): Int {
        var newAlpha = alpha

        if (depth == 0 || getWinner(game, move) != null) {
            return Evaluator.evaluateField(game, move, depth)
        }

        var value: Int
        var best = Int.MIN_VALUE

        val moves = getSortedPotentialMoves(game)

        for (currentMove in moves) {

            game.makeMove(currentMove)
            value = -negamax(game, currentMove, depth - 1, -beta, -newAlpha)
            game.undoMove(currentMove)

            if (value > best) {
                best = value
            }
            if (best > newAlpha) newAlpha = best
            if (best >= beta) {
                break
            }
        }
        return best
    }


    private fun getSortedPotentialMoves(game) {
        val moves = listOf<Move>()
    }

        private fun getPlayerSymbol(game: Game, isBot: Boolean): GameSymbol =
            if (isBot) game.memberSymbol else game.ownerSymbol

        private fun checkGameResult(game: Game, x: Int, y: Int, isBotTurn: Boolean): Int? {
            val symbol = getPlayerSymbol(game, isBotTurn)
            return when {
                isWinningMove(game, symbol, x, y) -> if (isBotTurn) 10 else -10
                isDraw(game) -> 0
                else -> null
            }
        }

}
