package com.test.tictactoe.enum

enum class GameStatus {
    NOT_STARTED,        // Игра еще не началась
    IN_PROGRESS,        // Игра идет
    CROSS_WON,   // Игрок за крестики выиграл
    ZERO_WON,    // Игрок за нолики выиграл
    DRAW,               // Игра закончилась ничьей
    ABORTED             // Игра была отменена
}