package com.test.tictactoe.utils.game

import com.test.tictactoe.controller.game.response.TournamentCreateResponse
import com.test.tictactoe.controller.game.response.TournamentStateResponse
import com.test.tictactoe.model.Tournament

fun Tournament.toTournamentCreateResponse(): TournamentCreateResponse =
    TournamentCreateResponse(
        id = this.id,
        playersCount = this.playersCount
    )

fun Tournament.toTournamentStateResponse() : TournamentStateResponse =
    TournamentStateResponse(
        ownerLogin = this.owner.login,
        playersCount = this.playersCount,
        playerLogins = this.players,
        roundGames = this.roundGames,
        isStarted = this.isStarted,
    )