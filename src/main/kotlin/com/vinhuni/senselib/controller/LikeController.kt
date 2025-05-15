package com.vinhuni.senselib.controller
import com.vinhuni.senselib.service.BookService
import com.vinhuni.senselib.service.LikeService
import com.vinhuni.senselib.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
class LikeController @Autowired constructor(
    private val likeService: LikeService,
    private val userService: UserService,
    private val bookService: BookService
) {
    @GetMapping("/favorites")
    fun getFavorites(model: Model, authentication: Authentication): String {
        val user = userService.getUserByUsername(authentication.name)
        if (user != null) {
            val likes = likeService.getUserLikes(user)
            model.addAttribute("favorites", likes)
        }
        return "favorites"
    }

    @PostMapping("/api/likes/toggle")
    @ResponseBody
    fun toggleLike(
        @RequestParam bookId: Int,
        authentication: Authentication
    ): ResponseEntity<Map<String, Any>> {
        println("Toggle like requested for book ID: $bookId, user: ${authentication.name}")

        val username = authentication.name
        val user = userService.getUserByUsername(username)
            ?: return ResponseEntity.badRequest().body(mapOf("error" to "User not found"))

        val book = bookService.getBookById(bookId)
            ?: return ResponseEntity.badRequest().body(mapOf("error" to "Book not found"))

        val isLiked = likeService.isLiked(user, book)
        println("Current like status for book $bookId: $isLiked")

        return if (isLiked) {
            likeService.removeLike(user, book)
            println("Like removed for book $bookId")
            ResponseEntity.ok(mapOf("status" to "removed"))
        } else {
            likeService.addLike(user, book)
            println("Like added for book $bookId")
            ResponseEntity.ok(mapOf("status" to "added"))
        }
    }

    @GetMapping("/api/likes/status")
    @ResponseBody
    fun getLikeStatus(
        @RequestParam bookId: Int,
        authentication: Authentication
    ): ResponseEntity<Map<String, Any>> {
        val username = authentication.name
        val user = userService.getUserByUsername(username)
            ?: return ResponseEntity.badRequest().body(mapOf("error" to "User not found"))

        val book = bookService.getBookById(bookId)
            ?: return ResponseEntity.badRequest().body(mapOf("error" to "Book not found"))

        val isLiked = likeService.isLiked(user, book)
        return ResponseEntity.ok(mapOf("isLiked" to isLiked))
    }
}