package com.vinhuni.senselib.controller.admin

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin")
class AdminController {
    @GetMapping("")
    fun adminDashboard(model: Model): String {
        return "admin/dashboard"
    }
}