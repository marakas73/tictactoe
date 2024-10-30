package com.test.tictactoe.service

import com.test.tictactoe.controller.game.request.GameCreateRequest
import com.test.tictactoe.enum.GameStatus
import com.test.tictactoe.enum.GameSymbol
import com.test.tictactoe.model.*
import com.test.tictactoe.repository.GameHistoryRepository
import com.test.tictactoe.repository.GameRepository
import com.test.tictactoe.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import com.test.tictactoe.utils.*

@Service
class GameService (
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository,
    private val gameHistoryRepository: GameHistoryRepository
) {
    suspend fun getGameState(
        playerLogin: String
    ): Game? = withContext(Dispatchers.IO) {
        val player = userRepository.findByLogin(playerLogin)

        player?.currentGame
    }

    suspend fun createGame(
        ownerLogin: String,
        request: GameCreateRequest
    ): Game? = withContext(Dispatchers.IO) {
        val owner = userRepository.findByLogin(ownerLogin)
        owner?.let {
            with(request) {
                val maxSize = maxOf(width, height)
                if (needToWin !in 3..maxSize) {
                    return@withContext null
                }
                if (ownerSymbol == memberSymbol) {
                    return@withContext null
                }

                val field = Field(
                    width = width,
                    height = height,
                )

                val game = if(isGameWithBot) Game(
                    owner = owner,
                    ownerSymbol = ownerSymbol,
                    memberSymbol = memberSymbol,
                    field = field,
                    needToWin = needToWin,
                    isGameWithBot = true,
                ) else Game(
                    owner = owner,
                    ownerSymbol = ownerSymbol,
                    memberSymbol = memberSymbol,
                    field = field,
                    needToWin = needToWin,
                    isGameWithBot = false,
                )

                owner.currentGame = game
                val savedUser = userRepository.save(owner)

                return@withContext savedUser.currentGame
            }
        }
    }

    suspend fun joinGame(
        gameId: Long,
        memberLogin: String
    ): Boolean = withContext(Dispatchers.IO) {
        val game = gameRepository.findById(gameId).orElse(null) ?: return@withContext false

        if(game.isGameWithBot)
            return@withContext false

        val member = userRepository.findByLogin(memberLogin) ?: return@withContext false

        if (game.member != null || member.isInGame)
            return@withContext false

        game.member = member
        member.currentGame = game

        userRepository.save(member)
        gameRepository.save(game)

        return@withContext true
    }

    suspend fun leaveGame(
        playerLogin: String
    ): Boolean = withContext(Dispatchers.IO) {
        val player = userRepository.findByLogin(playerLogin) ?: return@withContext false
        val game = player.currentGame ?: return@withContext false

        if(game.status == GameStatus.IN_PROGRESS || player == game.owner) {
            deleteGame(game)
        } else {
            player.currentGame = null
            game.member = null

            userRepository.save(player)
            gameRepository.save(game)
        }

        return@withContext true
    }

    suspend fun startGame(
        playerLogin: String
    ): Boolean = withContext(Dispatchers.IO) {
        val player = userRepository.findByLogin(playerLogin) ?: return@withContext false
        val playerGame = player.currentGame ?: return@withContext false

        if (!player.isInGame || playerGame.owner != player
            || playerGame.status == GameStatus.IN_PROGRESS
            || (playerGame.member == null && !playerGame.isGameWithBot)
        ) {
            return@withContext false
        }

        playerGame.status = GameStatus.IN_PROGRESS
        playerGame.status = if(playerGame.isGameWithBot && playerGame.currentMove == playerGame.memberSymbol) {
            val move: Pair<Int, Int> = GameBot.getOptimalMove(playerGame)
            handleMoveByBot(playerGame, playerGame.memberSymbol, move.first, move.second) ?: GameStatus.ABORTED

        } else GameStatus.IN_PROGRESS

        gameRepository.save(playerGame)


        return@withContext true
    }

    suspend fun handleMoveByPlayer(
        playerLogin: String,
        x: Int,
        y: Int
    ): GameStatus? = withContext(Dispatchers.IO) {
        val player = userRepository.findByLogin(playerLogin) ?: return@withContext null
        val game = player.currentGame ?: return@withContext null

        val playerMoveSymbol: GameSymbol =
            if(player == game.owner) game.ownerSymbol
            else game.memberSymbol

        if(!isMoveValid(playerMoveSymbol, game, x, y)) {
            return@withContext null
        }

        game.field.field[y][x] = playerMoveSymbol

        val newGameStatus = when {
            isWinningMove(game, playerMoveSymbol, x, y) -> handleWin(game)
            isDraw(game) -> handleDraw(game)
            else -> {
                changeCurrentMove(game)

                if(game.isGameWithBot) {
                    val move: Pair<Int, Int> = GameBot.getOptimalMove(game)
                    handleMoveByBot(game, game.memberSymbol, move.first, move.second)

                } else GameStatus.IN_PROGRESS
            }
        }

        // Set new status to game and save
        if(game.status != newGameStatus) {

            game.status = newGameStatus ?: return@withContext null
        }

        gameRepository.save(game)

        return@withContext newGameStatus
    }

    private fun handleMoveByBot(game: Game, botSymbol: GameSymbol, x: Int, y: Int) : GameStatus? {
        if(!isMoveValid(botSymbol, game, x, y)) {
            return null
        }

        game.field.field[y][x] = game.currentMove

        return when {
            isWinningMove(game, game.currentMove, x, y) -> handleWin(game)
            isDraw(game) -> handleDraw(game)
            else -> {
                changeCurrentMove(game)
                GameStatus.IN_PROGRESS
            }
        }
    }

    private fun handleWin(game: Game) : GameStatus? {
        if(!game.isGameWithBot) {
            val member = game.member ?: return null

            val winner = if (game.currentMove == game.ownerSymbol) game.owner else member
            val looser = if (winner == game.owner) member else game.owner

            updateRating(winner, looser)
            saveGameRecord(game.owner, member, winner, looser, false)
        }

        return if (game.currentMove == GameSymbol.CROSS)
            GameStatus.CROSS_WON
        else
            GameStatus.ZERO_WON
    }

    private fun handleDraw(game: Game) : GameStatus? {
        if(!game.isGameWithBot) {
            val member = game.member ?: return null

            updateRating(game.owner, member, true)
            saveGameRecord(game.owner, member, null, null, true)
        }

        return GameStatus.DRAW
    }

    private fun updateRating(winner: User, loser: User, isDraw: Boolean = false) {
        fun safeUpdate(winnerRatingBonus: Int, loserRatingBonus: Int) {
            winner.rating = maxOf(0, winner.rating + winnerRatingBonus)
            loser.rating = maxOf(0, loser.rating + loserRatingBonus)
        }

        if(isDraw) {
            safeUpdate(0, 0)
            return
        }

        safeUpdate(25, -25)
    }

    private fun saveGameRecord(player1: User, player2: User, winner: User?, looser: User?, isDraw: Boolean = false) {
        val gameRecord = GameRecord(
            player1 = player1,
            player2 = player2,
            winner = winner,
            looser = looser,
            isDraw = isDraw,
        )

        gameHistoryRepository.save(gameRecord)
    }

    private fun deleteGame(game: Game) {
        val member = game.member
        if(member != null) {
            member.currentGame = null
            userRepository.save(member)
        }

        game.owner.currentGame = null
        userRepository.save(game.owner)
        gameRepository.delete(game)
    }

    fun findGameById(id: Long): Game? {
        return gameRepository.findGameById(id)
    }
}