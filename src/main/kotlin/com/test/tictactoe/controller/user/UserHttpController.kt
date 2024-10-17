package com.test.tictactoe.controller.user

import com.test.tictactoe.controller.user.dto.GameRecordDTO
import com.test.tictactoe.controller.user.response.LeaderBoardResponse
import com.test.tictactoe.controller.user.response.UserInfoResponse
import com.test.tictactoe.exception.throwCannotGetGameHistoryException
import com.test.tictactoe.exception.throwCannotGetUserInfoException
import com.test.tictactoe.exception.throwCannotGetUserRatingPlaceException
import com.test.tictactoe.exception.throwForbidden
import com.test.tictactoe.service.TokenService
import com.test.tictactoe.service.UserService
import com.test.tictactoe.utils.toGameRecordDTO
import com.test.tictactoe.utils.toUserInfoResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserHttpController(
    private val tokenService: TokenService,
    private val userService: UserService
) {
    @GetMapping("/history")
    suspend fun getGameHistory(
        @RequestHeader("Authorization") authHeader: String
    ): List<GameRecordDTO> {
        val token = authHeader.substringAfter("Bearer ")
        val login = tokenService.extractLogin(token)?: throwForbidden()

        return userService.getGameHistory(
            login = login
        )
            ?.map{
                it.toGameRecordDTO()
            }
            ?: throwCannotGetGameHistoryException()
    }

    @GetMapping("/info")
    suspend fun getUserInfo(
        @RequestHeader("Authorization") authHeader: String
    ): UserInfoResponse {
        val token = authHeader.substringAfter("Bearer ")
        val login = tokenService.extractLogin(token)?: throwForbidden()

        return userService.getUserInfo(
            login = login
        )
            ?.toUserInfoResponse()
            ?: throwCannotGetUserInfoException()
    }

    @GetMapping("/leaderboard")
    suspend fun getLeaderBoard(
        @RequestHeader("Authorization") authHeader: String
    ): LeaderBoardResponse {
        val token = authHeader.substringAfter("Bearer ")
        val login = tokenService.extractLogin(token)?: throwForbidden()

        val leaderBoard = userService.getLeaderBoard()
        val playerPlace = userService.getPlayerLeaderBoardPlace(login) ?: throwCannotGetUserRatingPlaceException()

        return LeaderBoardResponse(
            playerPlace = playerPlace,
            leaderBoard = leaderBoard,
        )
    }
}