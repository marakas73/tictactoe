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

fun isWinningMove(game: Game, currentMoveSymbol: GameSymbol, move: Move): Boolean {
    return (countDirection(game, currentMoveSymbol, move.x, move.y, 1, 0)
            + countDirection(game, currentMoveSymbol, move.x, move.y, -1, 0) + 1 >= game.needToWin)
            || (countDirection(game, currentMoveSymbol, move.x, move.y, 0, -1)
            + countDirection(game, currentMoveSymbol, move.x, move.y, 0, 1) + 1 >= game.needToWin)
            || (countDirection(game, currentMoveSymbol, move.x, move.y, -1, -1)
            + countDirection(game, currentMoveSymbol, move.x, move.y, 1, 1) + 1 >= game.needToWin)
            || (countDirection(game, currentMoveSymbol, move.x, move.y, -1, 1)
            + countDirection(game, currentMoveSymbol, move.x, move.y, 1, -1) + 1 >= game.needToWin)
}

fun countDirection(game: Game, currentMoveSymbol: GameSymbol, x: Int, y: Int, deltaX: Int, deltaY: Int): Int {
    var counter = 0 // Not including (x,y) symbol
    var currentX = x + deltaX
    var currentY = y + deltaY

    while (
        isWithinBounds(game.field, currentX, currentY)
        && game.field.field[currentY][currentX] == currentMoveSymbol
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

fun countEmptyCells(field: List<MutableList<GameSymbol?>>): Int {
    return field.sumOf { list -> list.count { it == null } }
}

fun getPossibleMoves(game: Game): List<Pair<Int, Int>> {
    val possibleMoves: MutableList<Pair<Int, Int>> = mutableListOf()

    for (y in 0 until game.field.height) {
        for (x in 0 until game.field.width) {
            if (game.field.field[y][x] == null) {
                possibleMoves.add(Pair(x, y))
            }
        }
    }

    return possibleMoves.toList()
}

fun getPlayerSymbol(game: Game, isBot: Boolean): GameSymbol =
    if (isBot) game.memberSymbol else game.ownerSymbol

fun getWinner(game: Game, lastMove: Move): GameSymbol? {
    return when {
        isWinningMove(game, game.ownerSymbol, lastMove) -> game.ownerSymbol
        isWinningMove(game, game.memberSymbol, lastMove) -> game.memberSymbol
        else -> null
    }
}