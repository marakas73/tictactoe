package com.test.tictactoe.service

import com.test.tictactoe.controller.game.request.GameCreateRequest
import com.test.tictactoe.controller.game.request.TournamentCreateRequest
import com.test.tictactoe.enum.GameStatus
import com.test.tictactoe.enum.GameSymbol
import com.test.tictactoe.model.*
import com.test.tictactoe.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import com.test.tictactoe.utils.ai.GameBot
import com.test.tictactoe.utils.game.*

@Service
class GameService (
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository,
    private val gameHistoryRepository: GameHistoryRepository,
    private val tournamentRepository: TournamentRepository,
    private val roundRepository: RoundRepository
) {
    companion object {
        private const val gameHeight = 19
        private const val gameWidth = 19
        private const val gameNeedToWin = 5
        private val allowedTournamentPlayersCount = listOf(2,4, 8) //TODO to remove 2
    }

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
                val maxSize = maxOf(gameWidth, gameHeight)
                if (ownerSymbol == memberSymbol
                    || owner.tournament != null
                    ) {
                    return@withContext null
                }

                val field = Field(
                    width = gameWidth,
                    height = gameHeight,
                )

                val game = if(isGameWithBot) Game(
                    owner = owner,
                    ownerSymbol = ownerSymbol,
                    memberSymbol = memberSymbol,
                    field = field,
                    needToWin = gameNeedToWin,
                    isGameWithBot = true,
                ) else Game(
                    owner = owner,
                    ownerSymbol = ownerSymbol,
                    memberSymbol = memberSymbol,
                    field = field,
                    needToWin = gameNeedToWin,
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

        if (game.member != null || member.isInGame || member.tournament != null)
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

        if (!game.isTournament()) {
            if (game.status == GameStatus.IN_PROGRESS || player == game.owner) {
                deleteGame(game)
            } else {
                player.currentGame = null
                game.member = null

                userRepository.save(player)
                gameRepository.save(game)
            }
        } else {
            val member = game.member

            if(game.status == GameStatus.IN_PROGRESS && member != null){
                val techWinner = if(player == game.owner) member else game.owner

                val roundGame = roundRepository.findByGame(game) ?: return@withContext false

                setRoundWinner(techWinner, roundGame)

                createNewRoundsIfNeeded(roundGame.tournament)
            }

            val updatedGame = gameRepository.findGameById(game.id) ?: return@withContext  false
            deleteGame(updatedGame)
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
            val move: Move = GameBot.getBestMove(playerGame)
            handleMoveByBot(playerGame, playerGame.memberSymbol, move.x, move.y) ?: GameStatus.ABORTED

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
            isWinningMove(game, playerMoveSymbol, Move(x, y)) -> handleWin(game)
            isDraw(game) -> handleDraw(game)
            else -> {
                game.changeCurrentMove()

                if(game.isGameWithBot) {
                    val move: Move = GameBot.getBestMove(game)
                    handleMoveByBot(game, game.memberSymbol, move.x, move.y)
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

    suspend fun createTournament(
        ownerLogin: String,
        request: TournamentCreateRequest
    ): Tournament? = withContext(Dispatchers.IO) {
        val owner = userRepository.findByLogin(ownerLogin) ?: return@withContext null

        if(owner.tournament != null){
            return@withContext null
        }

        if(request.playersCount !in allowedTournamentPlayersCount){
            return@withContext null
        }

        val tournament = Tournament(
            owner = owner,
            playersCount = request.playersCount,
            players = mutableListOf(),
            roundGames = mutableListOf()
        )

        val savedTournament = tournamentRepository.save(tournament)
        owner.tournament = tournament

        userRepository.save(owner)

        return@withContext savedTournament
    }

    suspend fun joinTournament(
        playerLogin: String,
        tournamentId: Long
    ): Boolean = withContext(Dispatchers.IO) {
        val player = userRepository.findByLogin(playerLogin) ?: return@withContext false
        val tournament = tournamentRepository.findById(tournamentId).orElse(null) ?: return@withContext false

        if(player.tournament != null
            || player.isInGame
            || tournament.playersCount == tournament.players.size
            || tournament.started){
            return@withContext false
        }

        tournament.players.add(player)
        player.tournament = tournament

        userRepository.save(player)

        return@withContext true
    }

    suspend fun startTournament(
        playerLogin: String
    ): Boolean = withContext(Dispatchers.IO) {
        val player = userRepository.findByLogin(playerLogin) ?: return@withContext false
        val playerTournament = player.tournament ?: return@withContext false

        if(playerTournament.owner != player
            || playerTournament.started
            || playerTournament.players.size != playerTournament.playersCount
            ) {
            return@withContext false
        }

        playerTournament.started = true
        val savedTournament = tournamentRepository.save(playerTournament)
        createRounds(savedTournament)

        tournamentRepository.save(savedTournament)

        return@withContext true
    }

    suspend fun getTournamentState(
        tournamentId: Long,
    ): Tournament? = withContext(Dispatchers.IO) {
        tournamentRepository.findById(tournamentId).orElse(null)
    }

    private fun handleMoveByBot(game: Game, botSymbol: GameSymbol, x: Int, y: Int) : GameStatus? {
        if(!isMoveValid(botSymbol, game, x, y)) {
            return null
        }

        game.field.field[y][x] = game.currentMove

        return when {
            isWinningMove(game, game.currentMove, Move(x, y)) -> handleWin(game)
            isDraw(game) -> handleDraw(game)
            else -> {
                game.changeCurrentMove()
                GameStatus.IN_PROGRESS
            }
        }
    }

    private fun handleWin(game: Game) : GameStatus? {
        if(!game.isGameWithBot && !game.isTournament()) {
            val member = game.member ?: return null

            val winner = if (game.currentMove == game.ownerSymbol) game.owner else member
            val looser = if (winner == game.owner) member else game.owner

            updateRating(winner, looser)
            saveGameRecord(game.owner, member, winner, looser, false)
        }

        return if (game.currentMove == GameSymbol.CROSS) {
            if(game.isTournament()) {
                val roundGame = roundRepository.findByGame(game)
                if (roundGame != null) {
                    val winner: User? = if (game.ownerSymbol == GameSymbol.CROSS) game.owner else game.member
                    if(winner == null){
                        return GameStatus.ZERO_WON
                    }

                    setRoundWinner(winner, roundGame)

                    val tournament = roundGame.tournament
                    createNewRoundsIfNeeded(tournament)
                }
            }

            GameStatus.CROSS_WON
        }
        else
        {
            if(game.isTournament()) {
                val roundGame = roundRepository.findByGame(game)
                if (roundGame != null) {
                    val winner: User? = if (game.ownerSymbol == GameSymbol.ZERO) game.owner else game.member
                    if(winner == null){
                        return GameStatus.ZERO_WON
                    }

                    setRoundWinner(winner, roundGame)

                    val tournament = roundGame.tournament
                    createNewRoundsIfNeeded(tournament)
                }
            }

            GameStatus.ZERO_WON
        }
    }

    private fun setRoundWinner(winner: User, roundGame: RoundGame){
        roundGame.game = null
        roundGame.winner = winner

        roundRepository.save(roundGame)
    }

    private fun createNewRoundsIfNeeded(tournament: Tournament){
        // Check if all games have winner and make list
        val winners: MutableList<User> =
            tournament.roundGames
                .filter { it.round == tournament.currentRound }
                    .map { it.winner ?: return }
                        .toMutableList()

        for(player in tournament.players){
            if (winners.none { it.id == player.id }){
                player.tournament = null

                userRepository.save(player)
            }
        }

        // CHECK TOURNAMENT WINNER
        if(isTournamentEnded(winners, tournament)){
            return
        }

        val updatedTournament = tournamentRepository.findById(tournament.id).orElse(null) ?: return
        updatedTournament.currentRound++
        createRounds(updatedTournament)

        tournamentRepository.save(updatedTournament)
    }

    private fun isTournamentEnded(winners: List<User>, tournament: Tournament): Boolean {
        if (winners.size > 1) {
            return false
        }

        winners[0].tournament = null
        userRepository.save(winners[0])

        val updatedTournament = tournamentRepository.findById(tournament.id).orElse(null) ?: return false

        tournamentRepository.delete(updatedTournament)

        return true
    }

    private fun createRounds(tournament: Tournament){
        for(i in tournament.players.indices step 2){
            val field = Field(
                width = gameWidth,
                height = gameHeight,
            )

            val game = Game(
                owner = tournament.players[i],
                member = tournament.players[i + 1],
                ownerSymbol = GameSymbol.ZERO,
                memberSymbol = GameSymbol.CROSS,
                field = field,
                needToWin = gameNeedToWin,
                status = GameStatus.IN_PROGRESS,
                isGameWithBot = false,
            )

            tournament.players[i].currentGame = game
            tournament.players[i + 1].currentGame = game

            tournament.roundGames.add(
                RoundGame(
                    game = game,
                    tournament = tournament
                )
            )
        }
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