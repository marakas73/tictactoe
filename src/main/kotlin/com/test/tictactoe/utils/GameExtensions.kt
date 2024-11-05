package com.test.tictactoe.utils

import com.test.tictactoe.controller.game.response.GameCreateResponse
import com.test.tictactoe.controller.game.response.GameStateResponse
import com.test.tictactoe.model.Game

fun Game.toCreateResponse(): GameCreateResponse =
    GameCreateResponse(
        id = this.id,
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