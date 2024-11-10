package com.test.tictactoe.model

import jakarta.persistence.*

@Entity
@Table(name = "round_games")
data class RoundGame(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    var game: Game? = null,

    @OneToOne
    @JoinColumn(name = "winner_id", referencedColumnName = "id")
    var winner: User? = null,

    @Column
    val round: Int = 1,

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    val tournament: Tournament = Tournament()
)