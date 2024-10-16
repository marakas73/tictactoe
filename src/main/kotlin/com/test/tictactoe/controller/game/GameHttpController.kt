package com.test.tictactoe.controller.game

import com.test.tictactoe.controller.game.request.GameCreateRequest
import com.test.tictactoe.controller.game.request.GameMoveRequest
import com.test.tictactoe.controller.game.response.*
import com.test.tictactoe.enum.GameStatus
import com.test.tictactoe.service.GameService
import com.test.tictactoe.service.TokenService
import com.test.tictactoe.utils.toCreateResponse
import com.test.tictactoe.utils.toGameStateResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api/game")
class GameHttpController (
    private val gameService: GameService,
    private val tokenService: TokenService
) {

    @GetMapping("/state")
    suspend fun getGameState(
        @RequestHeader("Authorization") authHeader: String
    ): GameStateResponse {
        val token = authHeader.substringAfter("Bearer ")
        val login = tokenService.extractLogin(token) ?: throw ResponseStatusException(HttpStatus.FORBIDDEN)

        return gameService.getGameState(
            playerLogin = login
        )
            ?.toGameStateResponse()
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot get game state.")
    }

    @PostMapping("/create")
    suspend fun gameCreate(
        @RequestBody request: GameCreateRequest,
        @RequestHeader("Authorization") authHeader: String
        ) : GameCreateResponse {
        val token = authHeader.substringAfter("Bearer ")
        val login = tokenService.extractLogin(token) ?: throw ResponseStatusException(HttpStatus.FORBIDDEN)

        return gameService.createGame(
            ownerLogin = login,
            request = request
        )
            ?.toCreateResponse()
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create a game.")
    }

    @PostMapping("/move")
    suspend fun move(
        @RequestBody request: GameMoveRequest,
        @RequestHeader("Authorization") authHeader: String
    ): GameStatus {
        val token = authHeader.substringAfter("Bearer ")
        val login = tokenService.extractLogin(token) ?: throw ResponseStatusException(HttpStatus.FORBIDDEN)

        return gameService.makeMove(
            playerLogin = login,
            x = request.x,
            y = request.y,
        )
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Something wrong.")
    }

    @GetMapping("/join")
    suspend fun gameJoin(
        @RequestParam id: Long,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Unit> {
        val token = authHeader.substringAfter("Bearer ")
        val login = tokenService.extractLogin(token) ?: throw ResponseStatusException(HttpStatus.FORBIDDEN)

        return if(gameService.joinGame(
            gameId = id,
            memberLogin = login
        ))
            ResponseEntity(HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/leave")
    suspend fun gameLeave(
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Unit> {
        val token = authHeader.substringAfter("Bearer ")
        val login = tokenService.extractLogin(token) ?: throw ResponseStatusException(HttpStatus.FORBIDDEN)

        return if(gameService.leaveGame(
                playerLogin = login
            ))
            ResponseEntity(HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/start")
    suspend fun startGame(@RequestHeader("Authorization") authHeader: String): ResponseEntity<Unit> {
        val token = authHeader.substringAfter("Bearer ")
        val login = tokenService.extractLogin(token) ?: throw ResponseStatusException(HttpStatus.FORBIDDEN)

        return if(gameService.startGame(
                playerLogin = login
            ))
            ResponseEntity(HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.BAD_REQUEST)
    }
}