package com.test.tictactoe.service

import com.test.tictactoe.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

typealias ApplicationUser = com.test.tictactoe.model.User

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findByLogin(username)
            ?.mapToUserDetails()
            ?: throw UsernameNotFoundException("Not found!")
    }
    private fun ApplicationUser.mapToUserDetails(): UserDetails =
        User.builder()
            .username(this.login)
            .password(this.password)
            .build()
}