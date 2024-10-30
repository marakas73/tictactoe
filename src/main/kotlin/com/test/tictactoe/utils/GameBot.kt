package com.test.tictactoe.utils

import com.test.tictactoe.enum.GameSymbol
import com.test.tictactoe.model.Field
import com.test.tictactoe.model.Game

class GameBot {
    companion object {
        private const val DEFAULT_MAX_DEPTH = 10
        private const val MINIMAX_ENABLING_NUMBER = 25

        fun getOptimalMove(
            game: Game,
            depthOfCalculation: Int = DEFAULT_MAX_DEPTH
        ) : Pair<Int, Int> {
            val optimalMoves : MutableList<Pair<Int, Int>> = mutableListOf()
            var currentMaxMoveValue: Int = Int.MIN_VALUE

            getPossibleMoves(game).forEach { move ->
                val x = move.first
                val y = move.second


                val moveValue = minimaxAlgorithm(
                    gameCopy = game.copy(),
                    x = x,
                    y = y,
                    isBotTurn = true,
                    alpha = Int.MIN_VALUE,
                    beta = Int.MAX_VALUE,
                    currentDepth = 0
                )

                println("$x $y $moveValue") // TODO


                if(moveValue > currentMaxMoveValue) {
                    currentMaxMoveValue = moveValue
                    optimalMoves.clear()
                    optimalMoves.add(Pair(x, y))
                } else if (moveValue == currentMaxMoveValue) {
                    optimalMoves.add(Pair(x, y))
                }
            }

            return optimalMoves.random()
        }

        private fun minimaxAlgorithm(
            gameCopy: Game,
            x: Int,
            y: Int,
            isBotTurn: Boolean,
            alpha: Int,
            beta: Int,
            currentDepth: Int,
            maxDepth: Int = DEFAULT_MAX_DEPTH,
        ) : Int {

            if(currentDepth > maxDepth) return 0

            //println("$x $y $isBotTurn $currentDepth") // TODO

            val fieldCopy = gameCopy.field.field.map{it.toMutableList()}

            // Do move
            fieldCopy[y][x] = if(isBotTurn) gameCopy.memberSymbol else gameCopy.ownerSymbol

            // Check for end game
            when {
                isDraw(gameCopy) -> {
                    //println("draw") // TODO
                    return 0
                }
                isBotTurn -> {
                    if(isWinningMove(gameCopy, gameCopy.memberSymbol, x, y)) {
                        //println("win") // TODO
                        return 10
                    }


                }
                !isBotTurn -> {
                    if(isWinningMove(gameCopy, gameCopy.ownerSymbol, x, y)) {
                        //println("lose") // TODO
                        return -10
                    }


                }
            }

            changeCurrentMove(gameCopy)

            var currentAlpha = alpha
            var currentBeta = beta

            return if (isBotTurn) {
                var bestValue = Int.MIN_VALUE
                getPossibleMoves(gameCopy).forEach { move ->
                    val moveX = move.first
                    val moveY = move.second

                    // Do move
                    gameCopy.field.field[moveY][moveX] = gameCopy.memberSymbol

                    val value = minimaxAlgorithm(
                        gameCopy = gameCopy
                            .copy(
                                field = gameCopy.field
                                    .copy(
                                        field = gameCopy.field.field
                                            .map{it.toMutableList()}
                                    )
                            ),
                        x = moveX,
                        y = moveY,
                        isBotTurn = false,
                        alpha = currentAlpha,
                        beta = currentBeta,
                        currentDepth = currentDepth + 1
                    )

                    // Undo move
                    gameCopy.field.field[moveY][moveX] = null

                    bestValue = maxOf(bestValue, value)
                    currentAlpha = maxOf(currentAlpha, bestValue)
                    if (currentBeta <= currentAlpha) return bestValue  // Альфа-бета отсечение
                }
                bestValue
            } else {
                var bestValue = Int.MAX_VALUE
                getPossibleMoves(gameCopy).forEach { move ->
                    val moveX = move.first
                    val moveY = move.second

                    // Do move
                    gameCopy.field.field[moveY][moveX] = gameCopy.ownerSymbol

                    val value = minimaxAlgorithm(
                        gameCopy = gameCopy.copy(),
                        x = moveX,
                        y = moveY,
                        isBotTurn = true,
                        alpha = currentAlpha,
                        beta = currentBeta,
                        currentDepth = currentDepth + 1
                    )

                    // Undo move
                    gameCopy.field.field[moveY][moveX] = null

                    bestValue = minOf(bestValue, value)
                    currentBeta = minOf(currentBeta, bestValue)
                    if (currentBeta <= currentAlpha) return bestValue  // Альфа-бета отсечение
                }
                bestValue
            }
        }
    }
}