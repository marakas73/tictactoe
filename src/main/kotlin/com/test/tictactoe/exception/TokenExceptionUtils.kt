package com.test.tictactoe.exception

fun throwInvalidRefreshTokenException() : Nothing {
    throwForbidden("Invalid refresh token")
}
