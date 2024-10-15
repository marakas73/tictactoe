package com.test.tictactoe.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "game_history")
data class GameRecord (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "player1", referencedColumnName = "id")
    val player1: User,

    @ManyToOne
    @JoinColumn(name = "player2", referencedColumnName = "id")
    val player2: User,

    @ManyToOne
    @JoinColumn(name = "winner_id", referencedColumnName = "id")
    val winner: User?,

    @ManyToOne
    @JoinColumn(name = "looser_id", referencedColumnName = "id")
    var looser: User?,

    @Column
    val isDraw: Boolean
) {
    // Конструктор по умолчанию для JPA
    constructor() : this(0, User(), User(), null, null, true)

}