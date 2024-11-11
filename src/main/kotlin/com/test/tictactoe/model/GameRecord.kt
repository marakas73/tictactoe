package com.test.tictactoe.model

import jakarta.persistence.*

@Entity
@Table(name = "game_history")
data class GameRecord (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "player1", referencedColumnName = "id")
    val player1: User = User(),

    @ManyToOne
    @JoinColumn(name = "player2", referencedColumnName = "id")
    val player2: User = User(),

    @ManyToOne
    @JoinColumn(name = "winner_id", referencedColumnName = "id")
    val winner: User? = null,

    @ManyToOne
    @JoinColumn(name = "looser_id", referencedColumnName = "id")
    var looser: User? = null,

    @Column
    val isDraw: Boolean = true,

    @Column
    val isTournament: Boolean = false,

    @Column
    val gameId: Long = 0
)