package com.test.tictactoe.utils.ai

import com.test.tictactoe.model.Game
import com.test.tictactoe.utils.game.Move
import com.test.tictactoe.utils.game.changeCurrentMove
import com.test.tictactoe.utils.game.getWinner
import com.test.tictactoe.utils.hasAdjacent
import kotlin.math.max
import kotlin.math.min

object GameBot {
    private const val DEFAULT_MAX_DEPTH = 3

    fun getOptimalMove(
        game: Game,
        depth: Int = DEFAULT_MAX_DEPTH
    ): Pair<Int, Int> {
        val optimalMoves: MutableList<Pair<Int, Int>> = mutableListOf()
        var currentMaxMoveValue = Int.MIN_VALUE

        val field = game.field.field
        for (move in getSortedPotentialMoves(game)) {
            // Temp move
            field[move.y][move.x] = game.memberSymbol
            game.changeCurrentMove()

            val moveValue = minimax(
                move.x,
                move.y,
                alpha = Int.MIN_VALUE,
                beta = Int.MAX_VALUE,
                isBotTurn = false,
                depth = depth,
                game = game
            )

            // Remove temp move
            field[move.y][move.x] = null
            game.changeCurrentMove()

            if (moveValue > currentMaxMoveValue) {
                currentMaxMoveValue = moveValue
                optimalMoves.clear()
                optimalMoves.add(Pair(move.x, move.y))
            } else if (moveValue == currentMaxMoveValue) {
                optimalMoves.add(Pair(move.x, move.y))
            }
        }

        return optimalMoves.random()
    }

    private fun minimax(
        x: Int,
        y: Int,
        depth: Int,
        alpha: Int,
        beta: Int,
        isBotTurn: Boolean,
        game: Game
    ): Int {
        if (depth == 0 || getWinner(game, Move(x, y)) != null) {
            return Evaluator.evaluateState(game, Move(x, y), depth)
        }

        val field = game.field.field
        if (isBotTurn) {
            var maxEval = Int.MIN_VALUE

            for(move in getSortedPotentialMoves(game)) {
                // Temp move
                field[move.y][move.x] = game.memberSymbol
                game.changeCurrentMove()

                val eval = minimax(
                    x = move.x,
                    y = move.y,
                    depth = depth - 1,
                    alpha = alpha,
                    beta = beta,
                    isBotTurn = false,
                    game = game
                )

                // Remove temp move
                field[move.y][move.x] = null
                game.changeCurrentMove()

                maxEval = max(maxEval, eval)

                val newAlpha = max(alpha, eval)
                if (beta <= newAlpha)
                    return maxEval
            }

            return maxEval
        } else {
            var minEval = Int.MAX_VALUE

            for(move in getSortedPotentialMoves(game)) {
                // Temp move
                field[move.y][move.x] = game.ownerSymbol
                game.changeCurrentMove()

                val eval = minimax(
                    x = move.x,
                    y = move.y,
                    depth = depth - 1,
                    alpha = alpha,
                    beta = beta,
                    isBotTurn = true,
                    game = game
                )

                // Remove temp move
                field[move.y][move.x] = null
                game.changeCurrentMove()

                minEval = min(minEval, eval)
                val newBeta = min(beta, eval)
                if (newBeta <= alpha)
                    return minEval

            }

            return minEval
        }
    }

    private fun getSortedPotentialMoves(game: Game) : List<Move> {
/*

          // Board is empty, return a move in the middle of the board
          if (state.getMoves() === 0) {
              val moves: MutableList<Move> = ArrayList()
              moves.add(Move(state.board.length / 2, state.board.length / 2))
              return moves
          }

          val threatResponses: List<Move> = getThreatResponses(state)
          if (!threatResponses.isEmpty()) {
              return threatResponses
          }

*/

        val scoredMoves = mutableListOf<ScoredMove>()

        // Grab closest moves
        val moves = mutableListOf<Move>()
        for (y in 0 until game.field.height) {
            for (x in 0 until game.field.width) {
                if (game.field.field[y][x] == null) {
                    if (game.field.hasAdjacent(x, y)) {
                        val score: Int = Evaluator.evaluateCell(game.field, x, y, game.currentMove)
                        scoredMoves.add(ScoredMove(Move(x, y), score))
                    }
                }
            }
        }


        // Sort based on move score
        scoredMoves.sortBy { it.score }
        for (scoredMove in scoredMoves) {
            moves.add(scoredMove.move)
        }

        return moves.toList()
    }
}
