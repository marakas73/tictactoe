package com.test.tictactoe.utils

import com.test.tictactoe.enum.GameSymbol

fun GameSymbol.switch() : GameSymbol {
    return GameSymbol.entries[(this.ordinal + 1) % GameSymbol.entries.size]
}

fun GameSymbol.getIndex() : Int {
    return this.ordinal
}