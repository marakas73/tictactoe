package com.test.tictactoe.model

import com.test.tictactoe.enum.GameStatus
import com.test.tictactoe.enum.GameSymbol

class GameBot {
    companion object {
        fun getMove(
            game: Game,
            botSymbol: GameSymbol,
            isWinningMove: (game: Game, currentMoveSymbol: GameSymbol, x: Int, y: Int) -> Boolean
        ): Pair<Int, Int>? {
            if (game.status != GameStatus.IN_PROGRESS
                || game.currentMove != botSymbol
                || !game.isGameWithBot
            ) {
                return null
            }

            val possibleMoves: MutableList<Pair<Int, Int>> = mutableListOf()
            val possibleWinMoves: MutableList<Pair<Int, Int>> = mutableListOf()
            val possibleOpponentWinMoves: MutableList<Pair<Int, Int>> = mutableListOf()

            val field = game.field.field
            for (y in 0 until game.field.height) {
                for (x in 0 until game.field.width) {
                    if (field[y][x] == null) {

                        println(field[y][x]) // TODO
                        println(isWinningMove(game, game.ownerSymbol, x, y)) // TODO

                        val currentPair = Pair(x, y)

                        possibleMoves.add(currentPair)

                        if (isWinningMove(game, botSymbol, x, y)) {
                            possibleWinMoves.add(currentPair)
                        } else if (isWinningMove(game, game.ownerSymbol, x, y)) {
                            possibleOpponentWinMoves.add(currentPair)
                        }
                    }
                }
            }


            print('\n')
            // TODO
            print("possible")
            for (possibleMove in possibleMoves) {
                print(possibleMove)
            }
            print('\n')
            // TODO
            print("Win")

            for (possibleMove in possibleWinMoves) {
                print(possibleMove)
            }
            print('\n')// TODO
            print("Opponent")

            for (possibleMove in possibleOpponentWinMoves) {
                print(possibleMove)
            }
            print('\n')
            print('\n')


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
}