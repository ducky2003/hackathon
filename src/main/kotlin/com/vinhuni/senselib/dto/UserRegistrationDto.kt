package com.vinhuni.senselib.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class UserRegistrationDto(
    @field:NotEmpty(message = "Tên đăng nhập không được để trống")
    var username: String = "",

    @field:NotEmpty(message = "Họ tên không được để trống")
    var fullName: String = "",

    @field:NotEmpty(message = "Email không được để trống")
    @field:Email(message = "Email không hợp lệ")
    var email: String = "",

    @field:NotEmpty(message = "Mật khẩu không được để trống")
    @field:Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    var password: String = "",

    var confirmPassword: String = ""
)
