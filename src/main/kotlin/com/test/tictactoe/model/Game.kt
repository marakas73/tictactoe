package com.test.tictactoe.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.test.tictactoe.enum.GameStatus
import com.test.tictactoe.enum.GameSymbol
import jakarta.persistence.*

@Entity
@Table(name = "games")
data class Game(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    val owner: User,

    @OneToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    var member: User? = null,

    @Enumerated(EnumType.STRING)
    @Column
    val ownerSymbol: GameSymbol,

    @Enumerated(EnumType.STRING)
    @Column
    val memberSymbol: GameSymbol,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "field_id", referencedColumnName = "id")
    val field: Field,

    @Column
    val needToWin: Int,

    @Enumerated(EnumType.STRING)
    @Column
    var currentMove: GameSymbol = GameSymbol.CROSS,

    @Column
    var status: GameStatus = GameStatus.NOT_STARTED,
){
    // Конструктор по умолчанию для JPA
    constructor() : this(0, User(), null, GameSymbol.CROSS, GameSymbol.ZERO, Field(width = 3, height = 3), 3, GameSymbol.CROSS, GameStatus.NOT_STARTED)
}