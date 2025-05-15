package com.vinhuni.senselib.controller

import com.vinhuni.senselib.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.security.Principal

@Controller
class ProfileController(private val userService: UserService) {
    @GetMapping("/profile")
    fun showProfile(model: Model, principal: Principal?): String {
        if (principal == null) {
            return "redirect:/login"
        }

        val username = principal.name
        val currentUser = userService.getUserByUsername(username)

        model.addAttribute("user", currentUser)
        return "profile"
    }
}