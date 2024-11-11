package com.test.tictactoe.utils.game

import com.test.tictactoe.model.RoundGame

fun RoundGame.toSimpleRoundGame() : SimpleRoundGame =
    SimpleRoundGame(
        firstPlayerLogin = this.game?.owner?.login,
        secondPlayerLogin = this.game?.member?.login,
        winner = this.winner?.login,
    )