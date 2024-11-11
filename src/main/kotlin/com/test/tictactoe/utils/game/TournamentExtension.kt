package com.test.tictactoe.utils.game

import com.test.tictactoe.controller.game.response.TournamentCreateResponse
import com.test.tictactoe.controller.game.response.TournamentStateResponse
import com.test.tictactoe.model.Tournament
import kotlin.math.ceil
import kotlin.math.log
import kotlin.math.pow

fun Tournament.toTournamentCreateResponse(): TournamentCreateResponse =
    TournamentCreateResponse(
        id = this.id,
        playersCount = this.playersCount
    )

fun Tournament.toTournamentStateResponse() : TournamentStateResponse {
    val roundWinnersLogin = mutableMapOf<Int, MutableList<SimpleRoundGame?>>()
    val roundCount = ceil(log(this.playersCount.toDouble(), 2.0)).toInt()
    for(i in 1..roundCount) {
        val currentRoundWinners = mutableListOf<SimpleRoundGame?>()
        currentRoundWinners.addAll(
            this.roundGames
                .filter { it.round == i }
                    .map { it.toSimpleRoundGame() }
        )
        if(currentRoundWinners.size != 0) {
            roundWinnersLogin[i] = currentRoundWinners
        } else {
            val possibleWinnersCount = this.playersCount / ceil(2.0.pow(i)).toInt()
            roundWinnersLogin[i] = MutableList(possibleWinnersCount) { null }
        }

    }

    return TournamentStateResponse(
        ownerLogin = this.owner.login,
        playersCount = this.playersCount,
        playersLogin = this.players.map { it.login },
        roundGames = roundWinnersLogin,
        isStarted = this.isStarted,
    )
}