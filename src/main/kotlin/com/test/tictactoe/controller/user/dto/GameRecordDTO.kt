package com.test.tictactoe.controller.user.dto

import com.test.tictactoe.enum.GameSymbol

class GameRecordDTO(
    val id: Long,
    val player1Login: String,
    val player2Login: String,
    val winnerLogin: String?,
    val looserLogin: String?,
    val isDraw: Boolean,
    val isTournament: Boolean,
    val gameId: Long,
    val lastMoveX: Int,
    val lastMoveY: Int,
    val lastMoveSymbol: GameSymbol
)