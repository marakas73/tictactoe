package com.test.tictactoe.utils

import com.test.tictactoe.enum.GameSymbol
import com.test.tictactoe.model.Field
import com.test.tictactoe.model.Game
import kotlin.math.max
import kotlin.math.min

class GameBot {
    companion object {
        private const val DEFAULT_MAX_DEPTH = 10

        fun getOptimalMove(
            game: Game,
            depth: Int = DEFAULT_MAX_DEPTH
        ): Pair<Int, Int> {
            val optimalMoves: MutableList<Pair<Int, Int>> = mutableListOf()
            var currentMaxMoveValue = Int.MIN_VALUE

            val field = game.field.field
            for (rowIndex in field.indices) {
                for (colIndex in field[rowIndex].indices) {
                    // If cell isn't empty
                    if (field[rowIndex][colIndex] != null)
                        continue;

                    // Temp move
                    field[rowIndex][colIndex] = game.memberSymbol
                    val moveValue = minimax(
                        colIndex,
                        rowIndex,
                        alpha = Int.MIN_VALUE,
                        beta = Int.MAX_VALUE,
                        isBotTurn = false,
                        depth = depth,
                        game = game
                    )
                    // Remove temp move
                    field[rowIndex][colIndex] = null

                    if (moveValue > currentMaxMoveValue) {
                        currentMaxMoveValue = moveValue
                        optimalMoves.clear()
                        optimalMoves.add(Pair(colIndex, rowIndex))
                    } else if (moveValue == currentMaxMoveValue) {
                        optimalMoves.add(Pair(colIndex, rowIndex))
                    }
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
            if (depth == 0)
            {
                return 0
        }
            val gameResult = checkGameResult(game, x, y, !isBotTurn)
            if (gameResult != null)
                return gameResult


            val field = game.field.field;
            if (isBotTurn) {
                var maxEval = Int.MIN_VALUE

                for (rowIndex in field.indices) {
                    for (colIndex in field[rowIndex].indices) {
                        // If cell isn't empty
                        if (field[rowIndex][colIndex] != null)
                            continue

                        // Temp move
                        field[rowIndex][colIndex] = game.memberSymbol
                        val eval = minimax(
                            x = colIndex,
                            y = rowIndex,
                            depth = depth - 1,
                            alpha = alpha,
                            beta = beta,
                            isBotTurn = false,
                            game = game
                        )
                        // Remove temp move
                        field[rowIndex][colIndex] = null

                        maxEval = max(maxEval, eval)

                        val newAlpha = max(alpha, eval)
                        if (beta <= newAlpha)
                            return maxEval
                    }
                }

                return maxEval
            } else {
                var minEval = Int.MAX_VALUE

                for (rowIndex in field.indices) {
                    for (colIndex in field[rowIndex].indices) {
                        // If cell isn't empty
                        if (field[rowIndex][colIndex] != null)
                            continue;

                        // Temp move
                        field[rowIndex][colIndex] = game.ownerSymbol
                        val eval = minimax(
                            x = colIndex,
                            y = rowIndex,
                            depth = depth - 1,
                            alpha = alpha,
                            beta = beta,
                            isBotTurn = true,
                            game = game
                        )
                        // Remove temp move
                        field[rowIndex][colIndex] = null

                        minEval = min(minEval, eval)
                        val newBeta = min(beta, eval)
                        if (newBeta <= alpha)
                            return minEval
                    }
                }

                return minEval
            }
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
}
