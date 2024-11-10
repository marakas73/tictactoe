package com.test.tictactoe.exception

fun throwCannotCreateTournamentException() : Nothing {
    throwBadRequest("Cannot create a tournament.")
}

fun throwCannotGetTournamentState() : Nothing {
    throwBadRequest("Cannot get tournament state.")
}