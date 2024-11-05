package com.test.tictactoe.utils.ai

import com.test.tictactoe.enum.GameSymbol
import com.test.tictactoe.utils.getIndex
import com.test.tictactoe.utils.switch

class ThreatPattern(inputPattern: List<GameSymbol?>, patternSquares: Array<Int>) {
    private val pattern = Array(2) { List<GameSymbol?>(1) { null } }

    val patternSquares: Array<Int>

    init {
        this.pattern[0] = inputPattern
        this.pattern[1] = switchPattern(inputPattern)
        this.patternSquares = patternSquares
    }

    fun getPattern(playerSymbol: GameSymbol): List<GameSymbol?> {
        return pattern[playerSymbol.getIndex()]
    }

    private fun switchPattern(pattern: List<GameSymbol?>): List<GameSymbol?> {
        val patternSwitched = MutableList<GameSymbol?>(pattern.size) { null }
        for (i in pattern.indices) {
            patternSwitched[i] = pattern[i]?.switch()
        }

        return patternSwitched
    }
}