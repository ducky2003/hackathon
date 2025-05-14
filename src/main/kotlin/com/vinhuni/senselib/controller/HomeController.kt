package com.vinhuni.senselib.controller

import com.vinhuni.senselib.service.BookService
import com.vinhuni.senselib.service.CategoryService
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class HomeController(
    private val bookService: BookService,
    private val categoryService: CategoryService
) {
    @GetMapping("/home")
    fun home(model: Model, authentication: Authentication?,
             @RequestParam("page", defaultValue = "0") page: Int,
             @RequestParam("size", defaultValue = "6") size: Int,
             @RequestParam("category", required = false) categoryId: Int?): String {
        if (authentication != null) {
            model.addAttribute("username", authentication.name)
        }
        val paginatedBooks = if (categoryId == null) {
            bookService.getPaginatedBooks(page, size)
        } else {
            bookService.getPaginatedBooksByCategory(categoryId, page, size)
        }
        val categories = categoryService.getAllActiveCategories()
        val parentCategories = categoryService.getAllParentCategories()
        val childCategoriesMap = categoryService.getCategoryHierarchy()
        model.addAttribute("books", paginatedBooks.content)
        model.addAttribute("currentPage", paginatedBooks.number)
        model.addAttribute("totalPages", paginatedBooks.totalPages)
        model.addAttribute("categories", categories)
        model.addAttribute("categories", parentCategories)
        model.addAttribute("childCategories", childCategoriesMap)
        return "home"
    }

    @GetMapping("/")
    fun index(): String {
        return "redirect:/home"
    }
}