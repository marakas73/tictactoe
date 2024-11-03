package com.test.tictactoe.utils.ai

import com.test.tictactoe.enum.GameSymbol
import com.test.tictactoe.model.Field
import com.test.tictactoe.utils.game.Cell
import com.test.tictactoe.utils.game.Move
import com.test.tictactoe.utils.getAllDirections

internal object ThreatUtils {
    private val REFUTATIONS: MutableList<ThreatPattern> = ArrayList()
    private val THREES: MutableList<ThreatPattern> = ArrayList()
    private val FOURS: MutableList<ThreatPattern> = ArrayList()

    init {
        THREES.add(ThreatPattern(listOf(null, GameSymbol.CROSS, GameSymbol.CROSS, GameSymbol.CROSS, null, null), arrayOf(0, 4, 5)))
        THREES.add(ThreatPattern(listOf(null, null, GameSymbol.CROSS, GameSymbol.CROSS, GameSymbol.CROSS, null), arrayOf(0, 1, 5)))
        THREES.add(ThreatPattern(listOf(null, GameSymbol.CROSS, null, GameSymbol.CROSS, GameSymbol.CROSS, null), arrayOf(0, 2, 5)))
        THREES.add(ThreatPattern(listOf(null, GameSymbol.CROSS, GameSymbol.CROSS, null, GameSymbol.CROSS, null), arrayOf(0, 3, 5)))

        FOURS.add(ThreatPattern(listOf(GameSymbol.CROSS, GameSymbol.CROSS, GameSymbol.CROSS, GameSymbol.CROSS, null), arrayOf(4)))
        FOURS.add(ThreatPattern(listOf(GameSymbol.CROSS, GameSymbol.CROSS, GameSymbol.CROSS, null, GameSymbol.CROSS), arrayOf(3)))
        FOURS.add(ThreatPattern(listOf(GameSymbol.CROSS, GameSymbol.CROSS, null, GameSymbol.CROSS, GameSymbol.CROSS), arrayOf(2)))
        FOURS.add(ThreatPattern(listOf(GameSymbol.CROSS, null, GameSymbol.CROSS, GameSymbol.CROSS, GameSymbol.CROSS), arrayOf(1)))
        FOURS.add(ThreatPattern(listOf(null, GameSymbol.CROSS, GameSymbol.CROSS, GameSymbol.CROSS, GameSymbol.CROSS), arrayOf(0)))

        REFUTATIONS.add(ThreatPattern(listOf(GameSymbol.CROSS, GameSymbol.CROSS, GameSymbol.CROSS, null, null), arrayOf(3, 4)))
        REFUTATIONS.add(ThreatPattern(listOf(GameSymbol.CROSS, GameSymbol.CROSS, null, null, GameSymbol.CROSS), arrayOf(2, 3)))
        REFUTATIONS.add(ThreatPattern(listOf(GameSymbol.CROSS, null, null, GameSymbol.CROSS, GameSymbol.CROSS), arrayOf(1, 2)))
        REFUTATIONS.add(ThreatPattern(listOf(null, null, GameSymbol.CROSS, GameSymbol.CROSS, GameSymbol.CROSS), arrayOf(0, 1)))
    }


    fun getThrees(field: Field, x: Int, y: Int, symbol: GameSymbol): List<Move> {
        return getThreatMoves(THREES, field, x, y, symbol)
    }


    fun getFours(field: Field, x: Int, y: Int, symbol: GameSymbol): List<Move> {
        return getThreatMoves(FOURS, field, x, y, symbol)
    }


    fun getRefutations(field: Field, x: Int, y: Int, symbol: GameSymbol): List<Move> {
        return getThreatMoves(REFUTATIONS, field, x, y, symbol)
    }

    private fun getThreatMoves(
        patternList: List<ThreatPattern>,
        field: Field,
        x: Int,
        y: Int,
        playerSymbol: GameSymbol
    ): List<Move> {
        val threatMoves = mutableListOf<Move>()
        // Loop around the field in every direction
        // (diagonal/horizontal/vertical)
        for (direction in field.getAllDirections(x, y)) {
            for (pattern in patternList) {
                // Try to find the pattern
                val patternIndex = matchPattern(
                    direction.sequence,
                    pattern.getPattern(playerSymbol)
                )
                if (patternIndex != -1) {
                    // Found pattern, get the squares in the pattern and map
                    // them to moves on the board
                    for (patternSquareIndex in pattern.patternSquares) {
                        val patternSquareCell = direction.sequence[patternIndex + patternSquareIndex]
                        threatMoves.add(
                            Move(
                                patternSquareCell.x,
                                patternSquareCell.y
                            )
                        )
                    }
                }
            }
        }

        return threatMoves
    }

    private fun matchPattern(direction: List<Cell>, pattern: List<GameSymbol?>): Int {
        for (i in direction.indices) {
            // Check if the pattern lies within the bounds of the direction
            if (i + (pattern.size - 1) < direction.size) {
                var count = 0
                for (j in pattern.indices) {
                    if (direction[i + j].symbol == pattern[j]) {
                        count++
                    } else {
                        break
                    }
                }
                // Every element was the same, return the start index
                if (count == pattern.size) {
                    return i
                }
            } else {
                break
            }
        }

        return -1
    }
}