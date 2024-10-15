package com.test.tictactoe.service

import com.test.tictactoe.model.GameRecord
import com.test.tictactoe.model.User
import com.test.tictactoe.repository.GameHistoryRepository
import com.test.tictactoe.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val encoder: PasswordEncoder,
    private val gameHistoryRepository: GameHistoryRepository
) {
    fun createUser(user: User): User? {
        return if (!userRepository.existsByLogin(user.login))
            userRepository.save(user.copy(password = encoder.encode(user.password)))
        else
            null
    }

    fun getGameHistory(login: String) : List<GameRecord>? {
        val user = userRepository.findByLogin(login) ?: return null

        return gameHistoryRepository.getUserGameHistory(user)
    }

    fun getUserInfo(login: String) : User? {
        return userRepository.findByLogin(login)
    }
}