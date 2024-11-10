package com.test.tictactoe.repository

import com.test.tictactoe.model.Tournament
import org.springframework.data.jpa.repository.JpaRepository

interface TournamentRepository : JpaRepository<Tournament, Long> {
}