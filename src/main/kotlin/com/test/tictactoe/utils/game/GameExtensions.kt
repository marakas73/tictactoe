package com.test.tictactoe.utils.game

import com.test.tictactoe.controller.game.response.GameCreateResponse
import com.test.tictactoe.controller.game.response.GameStateResponse
import com.test.tictactoe.enum.GameSymbol
import com.test.tictactoe.model.Game
import com.test.tictactoe.utils.switch

fun Game.toCreateResponse(): GameCreateResponse =
    GameCreateResponse(
        id = this.id,
        width = this.field.width,
        height = this.field.height,
        needToWin = this.needToWin,
        ownerSymbol = this.ownerSymbol,
        memberSymbol = this.memberSymbol,
    )

fun Game.toGameStateResponse(): GameStateResponse {
    return GameStateResponse(
        field = this.field.field,
        currentMove = this.currentMove,
        gameStatus = this.status,
        ownerLogin = this.owner.login,
        memberLogin = this.member?.login,
        ownerSymbol = this.ownerSymbol,
        memberSymbol = this.memberSymbol,
        isGameWithBot = this.isGameWithBot,
    )
}

fun Game.changeCurrentMove() {
    this.currentMove = this.currentMove.switch()
}

fun Game.getNonCurrentMoveSymbol() : GameSymbol {
    return if(this.currentMove == this.ownerSymbol) this.memberSymbol else this.ownerSymbol
}

fun Game.getFullCopy() : Game {
    return this.copy(
        owner = this.owner.copy(),
        member = this.member?.copy(),
        field = this.field.copy(
            field = this.field.field.map { it.toMutableList() }
        ),
    )
}

fun Game.makeMove(move: Move) : Boolean {
    if(!isMoveValid(this.currentMove, this, move.x, move.y))
        return false

    this.field.field[move.y][move.x] = this.currentMove
    this.changeCurrentMove()
    return true
}

fun Game.undoMove(move: Move) : Boolean {
    if(!isWithinBounds(this.field, move.x, move.y))
        return false

    this.field.field[move.y][move.x] = null
    this.changeCurrentMove()
    return true
}