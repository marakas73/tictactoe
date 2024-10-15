package com.test.tictactoe.repository

import com.test.tictactoe.model.Game
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : JpaRepository<Game, Long> {
    fun findGameById(id: Long): Game?
}