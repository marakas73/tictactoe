package com.test.tictactoe.utils.game

import com.test.tictactoe.enum.GameStatus
import com.test.tictactoe.enum.GameSymbol
import com.test.tictactoe.model.Field
import com.test.tictactoe.model.Game

fun isMoveValid(moveSymbol: GameSymbol, game: Game, x: Int, y: Int) : Boolean {
    return (game.status == GameStatus.IN_PROGRESS
            && isWithinBounds(game.field, x, y)
            && game.field.field[y][x] == null
            && (moveSymbol == game.currentMove))
}

fun isDraw(game: Game): Boolean {
    return !game.field.field.any { innerList -> innerList.contains(null) }
}

fun isWinningMove(game: Game, playerSymbol: GameSymbol, move: Move): Boolean {
    return (countDirection(game, playerSymbol, move.x, move.y, 1, 0)
            + countDirection(game, playerSymbol, move.x, move.y, -1, 0) + 1 >= game.needToWin)
            || (countDirection(game, playerSymbol, move.x, move.y, 0, -1)
            + countDirection(game, playerSymbol, move.x, move.y, 0, 1) + 1 >= game.needToWin)
            || (countDirection(game, playerSymbol, move.x, move.y, -1, -1)
            + countDirection(game, playerSymbol, move.x, move.y, 1, 1) + 1 >= game.needToWin)
            || (countDirection(game, playerSymbol, move.x, move.y, -1, 1)
            + countDirection(game, playerSymbol, move.x, move.y, 1, -1) + 1 >= game.needToWin)
}

fun countDirection(game: Game, playerSymbol: GameSymbol, x: Int, y: Int, deltaX: Int, deltaY: Int): Int {
    var counter = 0 // Not including (x,y) symbol
    var currentX = x + deltaX
    var currentY = y + deltaY

    while (
        isWithinBounds(game.field, currentX, currentY)
        && game.field.field[currentY][currentX] == playerSymbol
    ) {
        counter++
        if (counter == game.needToWin) {
            return counter
        }
        currentX += deltaX
        currentY += deltaY
    }

    return counter
}

fun isWithinBounds(field: Field, x: Int, y: Int): Boolean {
    return x in 0 until field.width && y in 0 until field.height
}

fun getWinner(game: Game, lastMove: Move): GameSymbol? {
    return when {
        isWinningMove(game, game.ownerSymbol, lastMove) -> game.ownerSymbol
        isWinningMove(game, game.memberSymbol, lastMove) -> game.memberSymbol
        else -> null
    }
}