package com.test.tictactoe.utils

import com.test.tictactoe.enum.GameSymbol
import com.test.tictactoe.model.Game

class GameBot {
    companion object {
        private const val DEFAULT_DEPTH_OF_CALCULATION = 3

        fun getOptimalMove(
            game: Game,
            moveSymbol: GameSymbol,
            depthOfCalculation: Int = DEFAULT_DEPTH_OF_CALCULATION
        ) : Pair<Int, Int>? {
            var optimalMove : Pair<Int, Int>? = null
            var currentMaxScore: Int = Int.MIN_VALUE

            val field = game.field.field.map{it.toMutableList()}
            for (y in 0 until game.field.height) {
                for (x in 0 until game.field.width) {
                    if (field[y][x] == null) {
                        val score = getMoveScore(
                            game,
                            field,
                            x,
                            y,
                            moveSymbol,
                            currentMaxScore
                        )

                        if(score > currentMaxScore) {
                            currentMaxScore = score
                            optimalMove = Pair(x, y)
                        }
                    }
                }
            }

            return optimalMove
        }

        private fun getMoveScore(
            game: Game,
            intermediateField: List<MutableList<GameSymbol?>>,
            x: Int,
            y: Int,
            moveSymbol: GameSymbol,
            currentMaxScore: Int,
            currentScore: Int = 0,
            currentDepth: Int = 0,
            depthOfCalculation: Int = DEFAULT_DEPTH_OF_CALCULATION,
        ) : Int {
            val newCurrentDepth = currentDepth + 1
            if(newCurrentDepth > depthOfCalculation) {
                return currentScore
            }
            var newCurrentScore = currentScore
            intermediateField[y][x] =

            return currentScore
        }
    }
}