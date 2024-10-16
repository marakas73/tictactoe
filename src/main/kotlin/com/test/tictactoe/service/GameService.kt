package com.test.tictactoe.service

import com.test.tictactoe.controller.game.request.GameCreateRequest
import com.test.tictactoe.enum.GameStatus
import com.test.tictactoe.enum.GameSymbol
import com.test.tictactoe.model.Field
import com.test.tictactoe.model.Game
import com.test.tictactoe.model.GameRecord
import com.test.tictactoe.model.User
import com.test.tictactoe.repository.FieldRepository
import com.test.tictactoe.repository.GameHistoryRepository
import com.test.tictactoe.repository.GameRepository
import com.test.tictactoe.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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

                val game = Game(
                    owner = owner,
                    ownerSymbol = ownerSymbol,
                    memberSymbol = memberSymbol,
                    field = field,
                    needToWin = needToWin
                )

                owner.currentGame = game
                userRepository.save(owner)

                return@withContext game;
            }
        }
    }

    suspend fun joinGame(
        gameId: Long,
        memberLogin: String
    ): Boolean = withContext(Dispatchers.IO) {
        val game = gameRepository.findById(gameId).orElse(null) ?: return@withContext false
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
            || playerGame.status == GameStatus.IN_PROGRESS || playerGame.member == null
        ) {
            return@withContext false
        }

        playerGame.status = GameStatus.IN_PROGRESS
        gameRepository.save(playerGame)

        return@withContext true
    }

    @Transactional
    fun makeMove(
        playerLogin: String,
        x: Int,
        y: Int
    ): GameStatus? {
        val player = userRepository.findByLogin(playerLogin) ?: return null
        val game = player.currentGame ?: return null

        if (game.status != GameStatus.IN_PROGRESS || !isPositionValid(game, x, y)
            || game.field.field[y][x] != null
            || (player != game.owner && game.ownerSymbol == game.currentMove)
            || (player != game.member && game.memberSymbol == game.currentMove)
        ) {
            return null
        }

        game.field.field[y][x] = game.currentMove

        val isWin = isWinningMove(
            game = game,
            previousMove = game.currentMove,
            x = x,
            y = y
        )

        // Win
        if (isWin) {
            // Update rating and add to history
            // Owner win
            if(game.currentMove == game.ownerSymbol) {
                game.member?.let { member ->
                    updateRating(game.owner, member)
                    // Add to game history
                    val gameRecord = GameRecord(
                        player1 = game.owner,
                        player2 = member,
                        winner = game.owner,
                        looser = member,
                        isDraw = false
                    )

                    gameHistoryRepository.save(gameRecord)
                }
            // Member win
            } else {
                game.member?.let { member ->
                    updateRating(member, game.owner)
                    // Add to game history
                    val gameRecord = GameRecord(
                        player1 = game.owner,
                        player2 = member,
                        winner = member,
                        looser = game.owner,
                        isDraw = false
                    )

                    gameHistoryRepository.save(gameRecord)
                }
            }

            val answerStatus = if(game.currentMove == GameSymbol.CROSS) {
                GameStatus.CROSS_WON
            } else {
                GameStatus.ZERO_WON
            }

            game.status = answerStatus
            gameRepository.save(game)

            return answerStatus
        }

        // Draw
        if(isDraw(game)) {
            game.member?.let { member ->
                updateRating(game.owner, member, true)

                // Add to game history
                val gameRecord = GameRecord(
                    player1 = game.owner,
                    player2 = member,
                    winner = null,
                    looser = null,
                    isDraw = true
                )

                gameHistoryRepository.save(gameRecord)

                val answerStatus = GameStatus.DRAW

                game.status = answerStatus
                gameRepository.save(game)

                return answerStatus
            }
        }

        // Move does not affect game status
        changeCurrentMove(game)
        return GameStatus.IN_PROGRESS
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

    private fun isPositionValid(game: Game, x: Int, y: Int): Boolean {
        return !(x < 0 || x >= game.field.width || y < 0 || y >= game.field.height)
    }

    private fun changeCurrentMove(game: Game) {
        game.currentMove = GameSymbol.entries[(game.currentMove.ordinal + 1) % GameSymbol.entries.size]
    }

    private fun isDraw(game: Game): Boolean {
        return !game.field.field.any { innerList -> innerList.contains(null) }
    }

    fun isWinningMove(game: Game, previousMove: GameSymbol, x: Int, y: Int): Boolean {
        var counter: Int = 1
        var currentX: Int = x
        var currentY: Int = y


        // Horizontal
        counter = 1
        currentX = x
        currentY = y
        // Right
        while (currentX < game.field.width - 1) {
            currentX++
            if (game.field.field[currentY][currentX] == previousMove) {
                counter++
                if (counter == game.needToWin) {
                    return true
                }
            } else {
                break
            }
        }

        // Left
        currentX = x
        currentY = y
        while (currentX > 0) {
            currentX--
            if (game.field.field[currentY][currentX] == previousMove) {
                counter++
                if (counter == game.needToWin) {
                    return true
                }
            } else {
                break
            }
        }


        // Verticals
        counter = 1
        currentX = x
        currentY = y
        // Up
        while (currentY > 0) {
            currentY--
            if (game.field.field[currentY][currentX] == previousMove) {
                counter++
                if (counter == game.needToWin) {
                    return true
                }
            } else {
                break
            }
        }

        // Down
        currentX = x
        currentY = y
        while (currentY < game.field.height - 1) {
            currentY++
            if (game.field.field[currentY][currentX] == previousMove) {
                counter++
                if (counter == game.needToWin) {
                    return true
                }
            } else {
                break
            }
        }


        // main diagonal
        counter = 1
        currentX = x
        currentY = y
        // Up
        while (currentY > 0 && currentX > 0) {
            currentY--
            currentX--
            if (game.field.field[currentY][currentX] == previousMove) {
                counter++
                if (counter == game.needToWin) {
                    return true
                }
            } else {
                break
            }
        }

        // Down
        currentX = x
        currentY = y
        while (currentY < game.field.height - 1 && currentX < game.field.width - 1) {
            currentY++
            currentX++
            if (game.field.field[currentY][currentX] == previousMove) {
                counter++
                if (counter == game.needToWin) {
                    return true
                }
            } else {
                break
            }
        }


        // second diagonal
        counter = 1
        currentX = x
        currentY = y
        // Up
        while (currentY > 0 && currentX < game.field.width - 1) {
            currentY--
            currentX++
            if (game.field.field[currentY][currentX] == previousMove) {
                counter++
                if (counter == game.needToWin) {
                    return true
                }
            } else {
                break
            }
        }

        // Down
        currentX = x
        currentY = y
        while (currentY < game.field.height - 1 && currentX > 0) {
            currentY++
            currentX--
            if (game.field.field[currentY][currentX] == previousMove) {
                counter++
                if (counter == game.needToWin) {
                    return true
                }
            } else {
                break
            }
        }

        return false
    }

    fun findGameById(id: Long): Game? {
        return gameRepository.findGameById(id)
    }
}