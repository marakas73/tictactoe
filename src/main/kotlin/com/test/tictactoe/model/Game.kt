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
    val owner: User = User(),

    @OneToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    var member: User? = null,

    @Enumerated(EnumType.STRING)
    @Column
    val ownerSymbol: GameSymbol = GameSymbol.CROSS,

    @Enumerated(EnumType.STRING)
    @Column
    val memberSymbol: GameSymbol = GameSymbol.ZERO,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "field_id", referencedColumnName = "id")
    val field: Field = Field(),

    @Column
    val needToWin: Int = 3,

    @Enumerated(EnumType.STRING)
    @Column
    var currentMove: GameSymbol = GameSymbol.CROSS,

    @Column
    var status: GameStatus = GameStatus.NOT_STARTED,
){

}