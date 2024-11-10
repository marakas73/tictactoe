package com.test.tictactoe.repository

import com.test.tictactoe.model.Game
import com.test.tictactoe.model.RoundGame
import com.test.tictactoe.model.Tournament
import org.springframework.data.jpa.repository.JpaRepository

interface RoundRepository : JpaRepository<RoundGame, Long> {
    fun findByGame(game: Game): RoundGame?
}