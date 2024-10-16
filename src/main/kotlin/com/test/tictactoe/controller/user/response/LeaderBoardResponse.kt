package com.test.tictactoe.controller.user.response

data class LeaderBoardResponse (
    val playerPlace: Int,
    val leaderBoard: List<Pair<String, Int>>,
)