package com.vinhuni.senselib.controller

import com.vinhuni.senselib.dto.UserRegistrationDto
import com.vinhuni.senselib.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import jakarta.validation.Valid
@Controller
class AuthController(private val userService: UserService) {

    @GetMapping("/login")
    fun showLoginForm(
        model: Model,
        @RequestParam(required = false) error: String?,
        @RequestParam(required = false) logout: String?,
        @RequestParam(required = false) registered: String?
    ): String {
        if (error != null) {
            model.addAttribute("errorMsg", "Tên đăng nhập hoặc mật khẩu không chính xác")
        }

        if (logout != null) {
            model.addAttribute("logoutMsg", "Bạn đã đăng xuất thành công")
        }

        if (registered != null) {
            model.addAttribute("registeredMsg", "Đăng ký thành công, vui lòng đăng nhập")
        }

        model.addAttribute("user", UserRegistrationDto())
        return "login"
    }

    @GetMapping("/register")
    fun showRegistrationForm(model: Model): String {
        model.addAttribute("user", UserRegistrationDto())
        return "login"
    }

    @PostMapping("/register")
    fun registerUser(
        @RequestParam("username") username: String,
        @RequestParam("fullName") fullName: String,
        @RequestParam("email") email: String,
        @RequestParam("password") password: String,
        @RequestParam("confirmPassword") confirmPassword: String,
        model: Model
    ): String {
        // Kiểm tra mật khẩu xác nhận
        if (password != confirmPassword) {
            model.addAttribute("registrationError", "Mật khẩu xác nhận không khớp")
            model.addAttribute("user", UserRegistrationDto())
            return "login"
        }

        try {
            val userDto = UserRegistrationDto(
                username = username,
                fullName = fullName,
                email = email,
                password = password,
                confirmPassword = confirmPassword
            )
            userService.registerUser(userDto)
        } catch (e: Exception) {
            model.addAttribute("registrationError", e.message)
            model.addAttribute("user", UserRegistrationDto())
            return "login"
        }

        return "redirect:/login?registered=true"
    }
}