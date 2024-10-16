package com.test.tictactoe.controller.auth

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/auth")
class AuthWebController {
    @GetMapping("/register")
    suspend fun showRegistrationForm(): String {
        return "register"
    }

    @GetMapping("/login")
    suspend fun showAuthenticationForm(): String {
        return "login"
    }
}