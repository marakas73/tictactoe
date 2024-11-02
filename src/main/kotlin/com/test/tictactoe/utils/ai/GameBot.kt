package com.test.tictactoe.utils.ai

import com.test.tictactoe.model.Game
import com.test.tictactoe.utils.game.*
import com.test.tictactoe.utils.hasAdjacent
import com.test.tictactoe.utils.hasMoves
import java.util.HashSet
import kotlin.math.max
import kotlin.math.min

object GameBot {
    private const val DEFAULT_MAX_DEPTH = 2

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

    private fun getThreatResponses(game: Game): List<Move> {
        val playerSymbol = game.getCurrentMoveSymbol()
        val opponentSymbol = game.getNonCurrentMoveSymbol()

        val fours = HashSet<Move>()
        val threes = HashSet<Move>()
        val refutations = HashSet<Move>()

        val opponentFours = HashSet<Move>()
        val opponentThrees = HashSet<Move>()
        val opponentRefutations = HashSet<Move>()

        // Check for threats first and respond to them if they exist
        val field = game.field.field
        for (y in game.field.field.indices) {
            for (x in game.field.field[y].indices) {
                if (field[y][x] == opponentSymbol) {
                    opponentFours.addAll(
                        ThreatUtils.getFours(
                            game.field,
                            x,
                            y,
                            opponentSymbol
                        )
                    )
                    opponentThrees.addAll(
                        ThreatUtils.getThrees(
                            game.field,
                            x,
                            y,
                            opponentSymbol
                        )
                    )
                    opponentRefutations.addAll(
                        ThreatUtils.getRefutations(
                            game.field,
                            x,
                            y,
                            opponentSymbol
                        )
                    )
                } else if (field[y][x] == playerSymbol) {
                    fours.addAll(
                        ThreatUtils.getFours(
                            game.field,
                            x,
                            y,
                            playerSymbol
                        )
                    )
                    threes.addAll(
                        ThreatUtils.getThrees(
                            game.field,
                            x,
                            y,
                            playerSymbol
                        )
                    )
                    refutations.addAll(
                        ThreatUtils.getRefutations(
                            game.field,
                            x,
                            y,
                            playerSymbol
                        )
                    )
                }
            }
        }

        // We have a four on the board, play it
        if (fours.isNotEmpty()) {
            return fours.toList()
        }

        // Opponent has a four, defend against it
        if (opponentFours.isNotEmpty()) {
            return opponentFours.toList()
        }

        // We have a three that we can play to win.
        // Either we play the three and win, or our opponent has a refutation
        // that leads to their win. So we only consider our three and the
        // opponents refutations.
        if (threes.isNotEmpty()) {
            threes.addAll(opponentRefutations)
            return threes.toList()
        }

        // Opponent has a three, defend against it and add refutation moves
        if (opponentThrees.isNotEmpty()) {
            opponentThrees.addAll(refutations)
            return opponentThrees.toList()
        }

        return listOf()
    }

    private fun getSortedPotentialMoves(game: Game) : List<Move> {

        // Board is empty, return a move in the middle of the board
        if (!game.field.hasMoves()) {
            val moves: MutableList<Move> = ArrayList()
            moves.add(Move(game.field.height / 2, game.field.width / 2))
            return moves
        }

        val threatResponses: List<Move> = getThreatResponses(game)
        if (threatResponses.isNotEmpty()) {
            return threatResponses
        }

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
