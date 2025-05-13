package com.vinhuni.senselib.service

import com.vinhuni.senselib.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {
    private val logger = LoggerFactory.getLogger(UserDetailsServiceImpl::class.java)

    override fun loadUserByUsername(username: String): UserDetails {
        logger.info("Attempting to load user by username: {}", username)

        val user = try {
            userRepository.findByUsername(username)
        } catch (e: Exception) {
            logger.error("Error querying user: {}", e.message)
            null
        }

        if (user == null) {
            logger.error("User not found: {}", username)
            throw UsernameNotFoundException("Không tìm thấy người dùng: $username")
        }

        logger.info("User found: {}, role: {}", username, user.role?.roleName)

        // Check if role exists
        if (user.role == null || user.role?.roleName.isNullOrEmpty()) {
            logger.error("User has no role assigned: {}", username)
            throw UsernameNotFoundException("Người dùng không có quyền hạn")
        }

        val authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role?.roleName}"))

        return org.springframework.security.core.userdetails.User
            .withUsername(user.username!!)
            .password(user.password!!)
            .authorities(authorities)
            .accountExpired(user.accountStatus != "active")
            .accountLocked(user.accountStatus == "locked")
            .credentialsExpired(false)
            .disabled(user.accountStatus == "inactive")
            .build()
    }
}