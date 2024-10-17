package com.test.tictactoe.exception

fun throwCannotCreateUserException(): Nothing {
    throwBadRequest("Cannot create a user.")
}

fun throwCannotGetGameHistoryException() : Nothing {
    throwBadRequest("Cannot get game history.")
}

fun throwCannotGetUserInfoException() : Nothing {
    throwBadRequest("Cannot get user info.")
}

fun throwCannotGetUserRatingPlaceException() : Nothing {
    throwBadRequest("Cannot get user rating place.")
}