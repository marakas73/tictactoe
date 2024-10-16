package com.test.tictactoe.utils

import com.test.tictactoe.controller.user.dto.GameRecordDTO
import com.test.tictactoe.model.GameRecord

fun GameRecord.toGameRecordDTO(): GameRecordDTO =
    GameRecordDTO(
        id = id,
        player1Login = player1.login,
        player2Login = player2.login,
        winnerLogin = winner?.login,
        looserLogin = looser?.login,
        isDraw = isDraw
    )