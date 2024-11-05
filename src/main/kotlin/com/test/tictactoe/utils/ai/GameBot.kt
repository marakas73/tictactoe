package com.test.tictactoe.utils.ai

import com.test.tictactoe.model.Game
import com.test.tictactoe.utils.game.*
import com.test.tictactoe.utils.getMoves
import com.test.tictactoe.utils.hasAdjacent

object GameBot {
    private const val START_DEPTH = 2
    private const val END_DEPTH = 8
    private const val TIME_LIMIT_MILLIS = 5000

    private var handledScenarioCount = 0
    private var startTime = 0L

    private val logger = java.util.logging.Logger.getLogger("GameBot")

    private var timeNegamax = 0L
    private var timePatternFinding = 0L
    private var timeGetSortedPotentialMoves = 0L


    private fun printPerformanceInfo() {
        logger.info(
            "scenario counted: ${this.handledScenarioCount}"
        )
        logger.info(
            "time: " + (System.currentTimeMillis() - this.startTime)
        )

        logger.info("all negamax time: $timeNegamax")
        logger.info("all pattern finding time: $timePatternFinding")
        logger.info("all grab closest moves time: $timeGetSortedPotentialMoves")
    }

    private fun printSearchInfo(bestMove: Move, score: Int, depth: Int) {
        logger.info(
            "Depth: $depth, Evaluation: $score, Best move: (${bestMove.x} ${bestMove.y})"
        )
    }

    fun getBestMove(game: Game): Move {
        // Reset performance counts
        this.handledScenarioCount = 0
        this.startTime = System.currentTimeMillis()

        // Run a depth increasing search
        val bestMove = iterativeDeepening(game.getFullCopy())
        printPerformanceInfo()
        return bestMove
    }

    private fun iterativeDeepening(
        game: Game,
        startDepth: Int = START_DEPTH,
        endDepth: Int = END_DEPTH
    ): Move {
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
            val startTimeNegamax = System.currentTimeMillis()
            val score = -negamax(
                game,
                move,
                depth - 1,
                -beta,
                -alpha
            )
            timeNegamax += System.currentTimeMillis() - startTimeNegamax
            game.undoMove(move)

            scoredMoves.add(
                ScoredMove(
                    move = move,
                    score = score
                )
            )

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
        handledScenarioCount++
        if (Thread.interrupted() || (System.currentTimeMillis() - startTime) > TIME_LIMIT_MILLIS) {
            throw InterruptedException()
        }
        if (depth == 0 || getWinner(game, move) != null) {
            return Evaluator.evaluateField(game, move, depth)
        }

        var value: Int
        var best = Int.MIN_VALUE
        var newAlpha = alpha

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
        val startTimeGetSortedPotentialMoves = System.currentTimeMillis()

        // Board is empty, return a move in the middle of the board
        if (game.field.getMoves().isEmpty()) {
            val moves: MutableList<Move> = ArrayList()
            moves.add(Move(game.field.height / 2, game.field.width / 2))
            return moves
        }

        val startTimePatternFinding = System.currentTimeMillis()
        val threatResponses: List<Move> = getThreatResponses(game)
        timePatternFinding += System.currentTimeMillis() - startTimePatternFinding
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
        scoredMoves.sortByDescending { it.score }
        for (scoredMove in scoredMoves) {
            moves.add(scoredMove.move)
        }
        timeGetSortedPotentialMoves += System.currentTimeMillis() - startTimeGetSortedPotentialMoves

        return moves.toList()
    }
}
