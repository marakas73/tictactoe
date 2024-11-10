package com.test.tictactoe.model

import jakarta.persistence.*

@Entity
@Table(name = "tournaments")
data class Tournament(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    val owner: User = User(),

    @Column
    val playersCount: Int = 0,

    @Column
    @OneToMany(mappedBy = "tournament", fetch = FetchType.EAGER, cascade = [CascadeType.MERGE, CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH])
    var players: MutableList<User> = mutableListOf(),

    @OneToMany(mappedBy = "tournament", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    val roundGames: MutableList<RoundGame> = mutableListOf(),

    @Column
    var isStarted: Boolean = false
)