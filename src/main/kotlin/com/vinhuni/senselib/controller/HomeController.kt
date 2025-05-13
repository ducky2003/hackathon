package com.vinhuni.senselib.controller

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {
    @GetMapping("/home")
    fun home(model: Model, authentication: Authentication?): String {
        if (authentication != null) {
            model.addAttribute("username", authentication.name)
        }
        return "home"
    }

    @GetMapping("/")
    fun index(): String {
        return "redirect:/home"
    }
}