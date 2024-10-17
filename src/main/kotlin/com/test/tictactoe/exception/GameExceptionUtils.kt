package com.test.tictactoe.exception

fun throwCannotGetGameState() : Nothing {
    throwBadRequest("Cannot get game state.")
}

fun throwCannotCreateGameException() : Nothing {
    throwBadRequest("Cannot create a game.")
}

fun throwCannotExecuteMoveException() : Nothing {
    throwBadRequest("Cannot execute the move.")
}