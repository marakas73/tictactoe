package com.test.tictactoe.controller.home

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class HomeWebController (
) {
    @GetMapping("/")
    fun startPage(
    ): String{
        return "start-page"
    }

    @GetMapping("/home")
    fun home(): String{
        return "home"
    }

    @GetMapping("/history")
    fun gameHistory(): String{
        return "game-history"
    }

}