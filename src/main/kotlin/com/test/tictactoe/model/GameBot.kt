package com.test.tictactoe.model

import com.test.tictactoe.enum.GameStatus
import com.test.tictactoe.enum.GameSymbol

class GameBot (
    val botSymbol: GameSymbol,
    val isWinningMove: (game: Game, currentMoveSymbol: GameSymbol, x: Int, y: Int) -> Boolean,
) {
    fun getMove(game: Game) : Pair<Int, Int>? {
        if(game.status != GameStatus.IN_PROGRESS
            || game.currentMove != botSymbol
            || !game.isGameWithBot) {
            return null
        }

        val possibleMoves: MutableList<Pair<Int, Int>> = mutableListOf()
        val possibleWinMoves: MutableList<Pair<Int, Int>> = mutableListOf()
        val possibleOpponentWinMoves: MutableList<Pair<Int, Int>> = mutableListOf()

        val field = game.field.field
        for(i in 0 until game.field.height) {
            for(j in 0 until game.field.width) {
                if(field[i][j] == null) {
                    val currentPair = Pair(j, i)

                    possibleMoves.add(currentPair)

                    if(isWinningMove(game, botSymbol, j, i)) {
                        possibleWinMoves.add(currentPair)
                    } else if (possibleWinMoves.isEmpty()
                        && isWinningMove(game, game.ownerSymbol, j, i)
                        ) {
                        possibleOpponentWinMoves.add(currentPair)
                    }
                }
            }
        }

        return if (possibleWinMoves.isNotEmpty()) { // Return a random winning move if it exists
            possibleWinMoves.random()
        } else if (possibleOpponentWinMoves.isNotEmpty()) { // Otherwise, block the random opponent's winning move
            possibleOpponentWinMoves.random()
        } else if (possibleMoves.isNotEmpty()) { // If the opponent has no winning move, return a random possible move
            possibleMoves.random()
        } else { // If there are no moves at all, return null
            return null
        }
    }
}