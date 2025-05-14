package com.vinhuni.senselib.service

import com.vinhuni.senselib.dto.UserRegistrationDto
import com.vinhuni.senselib.model.User
import com.vinhuni.senselib.repository.RoleRepository
import com.vinhuni.senselib.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun getUserByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    fun saveUser(user: User): User {
        return userRepository.save(user)
    }
    fun registerUser(userDto: UserRegistrationDto): User {
        // Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(userDto.email)) {
            throw RuntimeException("Email đã được sử dụng")
        }
        if (userRepository.existsByUsername(userDto.username)) {
            throw RuntimeException("Tên đăng nhập đã được sử dụng")
        }
        // Lấy role mặc định (USER)
        val userRole = roleRepository.findByRoleName("member")
            ?: throw RuntimeException("Không tìm thấy vai trò mặc định")

        // Tạo user mới
        val user = User()
        user.username = userDto.username
        user.email = userDto.email
        user.fullName = userDto.fullName
        user.password = passwordEncoder.encode(userDto.password)
        user.role = userRole
        user.accountStatus = "active"
        user.createdAt = Instant.now()
        user.updatedAt = Instant.now()

        return userRepository.save(user)
    }
}