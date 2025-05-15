package com.vinhuni.senselib.service

import com.vinhuni.senselib.repository.UserRepository
import com.vinhuni.senselib.security.CustomUser
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found: $username")

        val authorities = listOf(SimpleGrantedAuthority(user.role?.roleName ?: "member"))

        return CustomUser.fromUserEntity(user, authorities)
    }
}