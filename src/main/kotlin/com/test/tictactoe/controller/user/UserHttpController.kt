package com.test.tictactoe.controller.user

import com.test.tictactoe.controller.game.response.GameCreateResponse
import com.test.tictactoe.controller.user.dto.GameRecordDTO
import com.test.tictactoe.controller.user.response.UserInfoResponse
import com.test.tictactoe.model.Game
import com.test.tictactoe.model.GameRecord
import com.test.tictactoe.model.User
import com.test.tictactoe.service.TokenService
import com.test.tictactoe.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/user")
class UserHttpController(
    private val tokenService: TokenService,
    private val userService: UserService
) {
    @GetMapping("/history")
    fun getGameHistory(
        @RequestHeader("Authorization") authHeader: String
    ): List<GameRecordDTO> {
        val token = authHeader.substringAfter("Bearer ")
        val login = tokenService.extractLogin(token)!!

        return userService.getGameHistory(
            login = login
        )
            ?.map{
                it.toGameRecordDTO()
            }
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot get game history.")
    }

    @GetMapping("/info")
    fun getUserInfo(
        @RequestHeader("Authorization") authHeader: String
    ): UserInfoResponse {
        val token = authHeader.substringAfter("Bearer ")
        val login = tokenService.extractLogin(token)!!

        return userService.getUserInfo(
            login = login
        )
            ?.toUserInfoResponse()
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot get user info.")
    }

    private fun GameRecord.toGameRecordDTO(): GameRecordDTO =
        GameRecordDTO(
            id = id,
            player1Login = player1.login,
            player2Login = player2.login,
            winnerLogin = winner?.login,
            looserLogin = looser?.login,
            isDraw = isDraw
        )

    private fun User.toUserInfoResponse(): UserInfoResponse =
        UserInfoResponse(
            id = this.id,
            login = this.login,
            rating = this.rating,
            currentGameId = this.currentGame?.id,
        )
}