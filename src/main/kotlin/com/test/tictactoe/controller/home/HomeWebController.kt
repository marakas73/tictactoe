package com.test.tictactoe.controller.home

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class HomeWebController (
) {
    @GetMapping("/")
    suspend fun startPage(
    ): String{
        return "start-page"
    }

    @GetMapping("/home")
    suspend fun home(): String{
        return "home"
    }

    @GetMapping("/history")
    suspend fun gameHistory(): String{
        return "game-history"
    }

}