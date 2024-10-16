package com.test.tictactoe.utils

import com.test.tictactoe.controller.game.response.GameCreateResponse
import com.test.tictactoe.controller.game.response.GameStateResponse
import com.test.tictactoe.model.Game

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
    )
}