package com.test.tictactoe.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

fun throwBadRequest(message: String): Nothing {
    throw ResponseStatusException(HttpStatus.BAD_REQUEST, message)
}

fun throwBadRequest(): Nothing {
    throw ResponseStatusException(HttpStatus.BAD_REQUEST)
}

fun throwForbidden(message: String): Nothing {
    throw ResponseStatusException(HttpStatus.FORBIDDEN, message)
}

fun throwForbidden(): Nothing {
    throw ResponseStatusException(HttpStatus.FORBIDDEN)
}