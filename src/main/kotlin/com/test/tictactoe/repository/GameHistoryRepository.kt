package com.test.tictactoe.repository

import com.test.tictactoe.model.Game
import com.test.tictactoe.model.GameRecord
import com.test.tictactoe.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface GameHistoryRepository : JpaRepository<GameRecord, Long> {
    @Query("SELECT gr FROM GameRecord gr WHERE gr.player1 = :user OR gr.player2 = :user")
    fun getUserGameHistory(@Param("user") user: User): List<GameRecord>
}