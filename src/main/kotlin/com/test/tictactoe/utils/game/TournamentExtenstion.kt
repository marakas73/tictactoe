package com.test.tictactoe.utils.game

import com.test.tictactoe.controller.game.response.TournamentCreateResponse
import com.test.tictactoe.controller.game.response.TournamentStateResponse
import com.test.tictactoe.model.Tournament
import kotlin.math.ceil
import kotlin.math.log

fun Tournament.toTournamentCreateResponse(): TournamentCreateResponse =
    TournamentCreateResponse(
        id = this.id,
        playersCount = this.playersCount
    )

fun Tournament.toTournamentStateResponse() : TournamentStateResponse {
    val roundWinnersLogin = mutableMapOf<Int, MutableList<String?>>()
    val roundCount = ceil(log(this.playersCount.toDouble(), 2.0)).toInt()
    for(i in 1..roundCount) {
        val currentRoundWinners = mutableListOf<String?>()
        currentRoundWinners.addAll(
            this.roundGames
                .filter { it.round == i }
                    .map { it.winner?.login }
        )
        roundWinnersLogin[i] = currentRoundWinners

    }

    return TournamentStateResponse(
        ownerLogin = this.owner.login,
        playersCount = this.playersCount,
        playersLogin = this.players.map { it.login },
        roundWinnersLogin = roundWinnersLogin,
        isStarted = this.isStarted,
    )
}