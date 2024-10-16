package com.test.tictactoe.service

import com.test.tictactoe.model.GameRecord
import com.test.tictactoe.model.User
import com.test.tictactoe.repository.GameHistoryRepository
import com.test.tictactoe.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val encoder: PasswordEncoder,
    private val gameHistoryRepository: GameHistoryRepository
) {
    suspend fun createUser(user: User): User? = withContext(Dispatchers.IO) {
        if (!userRepository.existsByLogin(user.login))
            userRepository.save(user.copy(password = encoder.encode(user.password)))
        else
            null
    }

    suspend fun getGameHistory(login: String) : List<GameRecord>? = withContext(Dispatchers.IO) {
        val user = userRepository.findByLogin(login)
        if(user != null) {
            gameHistoryRepository.getUserGameHistory(user)
        }
        else{
            null
        }
    }

    suspend fun getUserInfo(login: String) : User? = withContext(Dispatchers.IO) {
        userRepository.findByLogin(login)
    }

    suspend fun getLeaderBoard() : List<Pair<String, Int>> = withContext(Dispatchers.IO) {
        userRepository.findAllByOrderByRatingDesc().map { user ->
            Pair(user.login, user.rating)
        }
    }

    suspend fun getPlayerRatingPlace(login: String) : Int? = withContext(Dispatchers.IO) {
        val leaderBoard = getLeaderBoard()

        // Get player place in leader board
        leaderBoard.indexOfFirst { it.first == login }.takeIf { it >= 0 }?.plus(1)
    }
}