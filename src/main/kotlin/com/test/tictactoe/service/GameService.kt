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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GameService (
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository,
    private val fieldRepository: FieldRepository,
    private val gameHistoryRepository: GameHistoryRepository
) {
    fun getGameState(
        playerLogin: String
    ): Game? {
        val player = userRepository.findByLogin(playerLogin)!!

        if(!player.isInGame)
            return null

        return player.currentGame!!
    }

    @Transactional
    fun createGame(
        ownerLogin: String,
        request: GameCreateRequest
    ): Game? {
        val owner = userRepository.findByLogin(ownerLogin)!!

        if (owner.isInGame)
            return null

        with(request) {
            val maxSize = maxOf(width, height)
            if (needToWin !in 3..maxSize) {
                return null
            }
            if (ownerSymbol == memberSymbol) {
                return null
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

            return game;
        }
    }

    @Transactional
    fun joinGame(
        gameId: Long,
        memberLogin: String
    ): Boolean {
        val game = gameRepository.findById(gameId).orElse(null) ?: return false
        val member = userRepository.findByLogin(memberLogin) ?: return false

        if (game.member != null || member.isInGame)
            return false

        game.member = member
        member.currentGame = game

        return true
    }

    @Transactional
    fun leaveGame(
        playerLogin: String
    ): Boolean {
        val player = userRepository.findByLogin(playerLogin) ?: return false

        if (!player.isInGame)
            return false

        val game = player.currentGame!!

        if(game.status == GameStatus.IN_PROGRESS || player == game.owner) {
            deleteGame(game)
        } else {
            player.currentGame = null
            game.member = null
        }

        return true
    }

    @Transactional
    fun startGame(
        ownerLogin: String
    ): Boolean {
        val owner = userRepository.findByLogin(ownerLogin) ?: return false

        if (!owner.isInGame || owner.currentGame!!.owner != owner
            || owner.currentGame!!.status == GameStatus.IN_PROGRESS || owner.currentGame!!.member == null
        ) {
            return false
        }

        owner.currentGame!!.status = GameStatus.IN_PROGRESS
        return true
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
            if(game.currentMove == game.ownerSymbol) {
                updateRating(game.owner, game.member!!)
                // Add to game history
                val gameRecord = GameRecord(
                    player1 = game.owner,
                    player2 = game.member!!,
                    winner = game.owner,
                    looser = game.member,
                    isDraw = false
                )

                gameHistoryRepository.save(gameRecord)
            } else {
                updateRating(game.member!!, game.owner)
                // Add to game history
                val gameRecord = GameRecord(
                    player1 = game.owner,
                    player2 = game.member!!,
                    winner = game.member,
                    looser = game.owner,
                    isDraw = false
                )

                gameHistoryRepository.save(gameRecord)
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
            updateRating(game.owner, game.member!!, true)

            // Add to game history
            val gameRecord = GameRecord(
                player1 = game.owner,
                player2 = game.member!!,
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

        // Move does not affect game status
        changeCurrentMove(game)
        return GameStatus.IN_PROGRESS
    }

    private fun deleteGame(game: Game) {
        if(game.member != null) {
            game.member!!.currentGame = null
            userRepository.save(game.member!!)
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