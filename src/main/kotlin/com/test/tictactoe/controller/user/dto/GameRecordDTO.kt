package com.test.tictactoe.controller.user.dto

class GameRecordDTO(
    val id: Long,
    val player1Login: String,
    val player2Login: String,
    val winnerLogin: String?,
    val looserLogin: String?,
    val isDraw: Boolean
)