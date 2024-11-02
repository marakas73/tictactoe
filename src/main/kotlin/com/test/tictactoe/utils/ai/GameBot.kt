package com.test.tictactoe.utils.ai

import com.test.tictactoe.model.Game
import com.test.tictactoe.utils.game.*
import com.test.tictactoe.utils.getMoves
import com.test.tictactoe.utils.hasAdjacent
import kotlin.math.max
import kotlin.math.min

object GameBot {
    private const val START_DEPTH = 2
    private const val END_DEPTH = 8

    private var handledScenarioCount = 0 // TODO
    private var startTime = 0L // TODO

    fun getBestMove(game: Game): Move {
        // Reset performance counts
        this.handledScenarioCount = 0
        this.startTime = System.currentTimeMillis()

        // Run a depth increasing search
        val bestMove = iterativeDeepening(game.getFullCopy(), START_DEPTH, END_DEPTH)
        printPerformanceInfo()
        return bestMove
    }

    private fun printPerformanceInfo() {
        java.util.logging.Logger.getAnonymousLogger().info("\"scenario counted: \" + this.handledScenarioCount")
        java.util.logging.Logger.getAnonymousLogger().info("time: " + (System.currentTimeMillis() - this.startTime))
    }

    private fun printSearchInfo(bestMove: Move, score: Int, depth: Int) {
        java.util.logging.Logger.getAnonymousLogger().info(
            "Depth: $depth, Evaluation: $score, Best move: (${bestMove.x} ${bestMove.y})"
        )
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
        printSearchInfo(scoredMoves[0].move, scoredMoves[0].score, depth)

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
        handledScenarioCount++

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

    private fun minimax(
        x: Int,
        y: Int,
        depth: Int,
        alpha: Int,
        beta: Int,
        isBotTurn: Boolean,
        game: Game
    ): Int {


        handledScenarioCount++ // TODO



        if (depth == 0 || getWinner(game, Move(x, y)) != null) {
            return Evaluator.evaluateField(game, Move(x, y), depth)
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
                /*if (beta <= newAlpha) {
                    //return maxEval TODO
                }*/
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
                /*if (newBeta <= alpha) {
                    // return minEval TODO
                }*/
            }

            return minEval
        }
    }

    private fun getThreatResponses(game: Game): List<Move> {
        val playerSymbol = game.currentMove
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
        if (game.field.getMoves().isEmpty()) {
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
