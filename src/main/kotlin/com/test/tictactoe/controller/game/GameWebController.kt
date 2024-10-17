package com.test.tictactoe.controller.game

import com.test.tictactoe.exception.throwBadRequest
import com.test.tictactoe.exception.throwForbidden
import com.test.tictactoe.service.GameService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
class GameWebController (
    private val gameService: GameService,
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
}