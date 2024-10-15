package com.test.tictactoe.model

import jakarta.persistence.*
import jakarta.validation.constraints.Min
import kotlin.jvm.Transient

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true)
    val login: String,

    @Column
    val password: String,

    @Column
    @Min(0)
    var rating: Int = 1000,

    @ManyToOne(cascade = [CascadeType.ALL])
    var currentGame: Game? = null
) {

    val isInGame: Boolean
        get() = currentGame != null

    // Конструктор по умолчанию для JPA
    constructor() : this(0, "", "", 1000, null)
}