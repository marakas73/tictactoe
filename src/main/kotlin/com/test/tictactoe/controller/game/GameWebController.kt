package com.test.tictactoe.controller.game

import com.test.tictactoe.controller.game.request.GameCreateRequest
import com.test.tictactoe.controller.game.response.*
import com.test.tictactoe.service.GameService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
class GameWebController (
    private val gameService: GameService,
) {
    @GetMapping("/game")
    fun gamePage(
        @RequestParam id: Long,
        model: Model
    ): String {
        val game = gameService.findGameById(id)
        model.addAttribute("game", game)
        return "game"
    }

    @GetMapping("/game/create")
    fun gameCreate() : String {
        return "game-create"
    }
}