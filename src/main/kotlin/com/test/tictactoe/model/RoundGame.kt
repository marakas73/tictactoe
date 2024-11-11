package com.test.tictactoe.model

import jakarta.persistence.*

@Entity
@Table(name = "round_games")
data class RoundGame(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    var game: Game? = null,

    @ManyToOne
    @JoinColumn(name = "winner_id", referencedColumnName = "id")
    var winner: User? = null,

    @Column
    var round: Int = 0,

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    val tournament: Tournament = Tournament()
)