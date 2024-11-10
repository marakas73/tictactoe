package com.test.tictactoe.controller.game

import com.test.tictactoe.controller.game.request.GameCreateRequest
import com.test.tictactoe.controller.game.request.GameMoveRequest
import com.test.tictactoe.controller.game.request.TournamentCreateRequest
import com.test.tictactoe.controller.game.response.*
import com.test.tictactoe.enum.GameStatus
import com.test.tictactoe.exception.*
import com.test.tictactoe.service.GameService
import com.test.tictactoe.service.TokenService
import com.test.tictactoe.utils.game.toCreateResponse
import com.test.tictactoe.utils.game.toGameStateResponse
import com.test.tictactoe.utils.game.toTournamentCreateResponse
import com.test.tictactoe.utils.game.toTournamentStateResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
        val login = tokenService.extractLogin(token) ?: throwForbidden()

        return gameService.getGameState(
            playerLogin = login
        )
            ?.toGameStateResponse()
            ?: throwCannotGetGameState()
    }

    @PostMapping("/create")
    suspend fun gameCreate(
        @RequestBody request: GameCreateRequest,
        @RequestHeader("Authorization") authHeader: String
    ): GameCreateResponse {
        val token = authHeader.substringAfter("Bearer ")
        val login = tokenService.extractLogin(token) ?: throwForbidden()

        return gameService.createGame(
            ownerLogin = login,
            request = request
        )
            ?.toCreateResponse()
            ?: throwCannotCreateGameException()
    }

    @PostMapping("/move")
    suspend fun move(
        @RequestBody request: GameMoveRequest,
        @RequestHeader("Authorization") authHeader: String
    ): GameStatus {
        val token = authHeader.substringAfter("Bearer ")
        val login = tokenService.extractLogin(token) ?: throwForbidden()

        return gameService.handleMoveByPlayer(
            playerLogin = login,
            x = request.x,
            y = request.y,
        )
            ?: throwCannotExecuteMoveException()
    }

    @GetMapping("/join")
    suspend fun gameJoin(
        @RequestParam id: Long,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Unit> {
        val token = authHeader.substringAfter("Bearer ")
        val login = tokenService.extractLogin(token) ?: throwForbidden()

        return if (gameService.joinGame(
                gameId = id,
                memberLogin = login
            )
        )
            ResponseEntity(HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/leave")
    suspend fun gameLeave(
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Unit> {
        val token = authHeader.substringAfter("Bearer ")
        val login = tokenService.extractLogin(token) ?: throwForbidden()

        return if (gameService.leaveGame(
                playerLogin = login
            )
        )
            ResponseEntity(HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/start")
    suspend fun startGame(@RequestHeader("Authorization") authHeader: String): ResponseEntity<Unit> {
        val token = authHeader.substringAfter("Bearer ")
        val login = tokenService.extractLogin(token) ?: throwForbidden()

        return if (gameService.startGame(
                playerLogin = login
            )
        )
            ResponseEntity(HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @PostMapping("/tournament/create")
    suspend fun createTournament(
        @RequestHeader("Authorization") authHeader: String,
        @RequestBody request: TournamentCreateRequest
    ): TournamentCreateResponse {
        val token = authHeader.substringAfter("Bearer ")
        val login = tokenService.extractLogin(token) ?: throwForbidden()

        return gameService.createTournament(
            ownerLogin = login,
            request = request
        )
            ?.toTournamentCreateResponse()
            ?: throwCannotCreateTournamentException()
    }

    @GetMapping("/tournament/join")
    suspend fun tournamentJoin(
        @RequestParam id: Long,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Unit> {
        val token = authHeader.substringAfter("Bearer ")
        val login = tokenService.extractLogin(token) ?: throwForbidden()

        return if (gameService.joinTournament(
                tournamentId = id,
                playerLogin = login
            )
        )
            ResponseEntity(HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/tournament/start")
    suspend fun startTournament(@RequestHeader("Authorization") authHeader: String): ResponseEntity<Unit> {
        val token = authHeader.substringAfter("Bearer ")
        val login = tokenService.extractLogin(token) ?: throwForbidden()

        return if (gameService.startTournament(
                playerLogin = login
            )
        )
            ResponseEntity(HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/tournament/state")
    suspend fun getTournamentState(
        @RequestParam id: Long,
    ): TournamentStateResponse {
        return gameService.getTournamentState(
            tournamentId = id
        )
            ?.toTournamentStateResponse()
            ?: throwCannotGetTournamentState()
    }
}