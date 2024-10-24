package com.test.tictactoe.model

import com.test.tictactoe.enum.GameSymbol
import jakarta.persistence.*
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min


@Entity
@Table(name = "fields")
data class Field(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column
    @Min(3)
    @Max(100)
    val width: Int = 0,

    @Column
    @Min(3)
    @Max(100)
    val height: Int = 0,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "field_cells", joinColumns = [JoinColumn(name = "field_id")])
    @Column(name = "symbol")
    val field: List<MutableList<GameSymbol?>> = List(height) { MutableList(width) { null } }
)