package com.test.tictactoe.controller.game

import com.test.tictactoe.repository.TournamentRepository
import com.test.tictactoe.service.GameService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
class GameWebController (
    private val gameService: GameService,
    private val tournamentRepository: TournamentRepository,
) {
    @GetMapping("/game")
    suspend fun gamePage(
        @RequestParam id: Long,
        model: Model
    ): String {
        val game = gameService.findGameById(id)
        model.addAttribute("game", game)
        return "game"
    }

    @GetMapping("/game/create")
    suspend fun gameCreate() : String {
        return "game-create"
    }

    @GetMapping("/tournament")
    suspend fun tournamentPage(
        @RequestParam id: Long,
    ): String {
        return "tournament"
    }

    @GetMapping("/tournament/create")
    suspend fun tournamentCreate() : String {
        return "tournament-create"
    }
}