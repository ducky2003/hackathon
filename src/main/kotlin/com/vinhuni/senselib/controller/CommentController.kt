package com.vinhuni.senselib.controller
import com.vinhuni.senselib.model.User
import com.vinhuni.senselib.repository.UserRepository
import com.vinhuni.senselib.service.CommentService
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/comments")
class CommentController(
    private val commentService: CommentService,
    private val userRepository: UserRepository

) {
    @PostMapping("/add")
    @ResponseBody
    fun addComment(
        @RequestParam("bookId") bookId: Int,
        @RequestParam("text") text: String,
        @RequestParam("parentId", required = false) parentId: Int?,
        authentication: Authentication
    ): ResponseEntity<Map<String, Any>> {
        val username = authentication.name
        val currentUser = userRepository.findByUsername(username)
            ?: return ResponseEntity.badRequest().body(mapOf("status" to "error", "message" to "User not found"))

        val comment = commentService.addComment(bookId, text, currentUser, parentId)

        val responseMap = HashMap<String, Any>()
        responseMap["status"] = "success"
        responseMap["commentId"] = comment.id!!
        responseMap["userName"] = (currentUser.fullName ?: currentUser.username) as Any
        responseMap["userAvatar"] = (currentUser.avatar ?: "/img/default-avatar.jpg") as Any
        responseMap["createdAt"] = comment.createdAt!!

        return ResponseEntity.ok(responseMap)
    }

    // Update the other methods similarly
    @PutMapping("/update/{id}")
    @ResponseBody
    fun updateComment(
        @PathVariable("id") commentId: Int,
        @RequestParam("text") text: String,
        authentication: Authentication
    ): ResponseEntity<Map<String, Any>> {
        val username = authentication.name
        val currentUser = userRepository.findByUsername(username)
            ?: return ResponseEntity.badRequest().body(mapOf("status" to "error", "message" to "User not found"))

        val comment = commentService.updateComment(commentId, text, currentUser)

        return ResponseEntity.ok(mapOf(
            "status" to "success",
            "commentId" to comment.id!!
        ))
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    fun deleteComment(
        @PathVariable("id") commentId: Int,
        authentication: Authentication
    ): ResponseEntity<Map<String, Any>> {
        val username = authentication.name
        val currentUser = userRepository.findByUsername(username)
            ?: return ResponseEntity.badRequest().body(mapOf("status" to "error", "message" to "User not found"))

        commentService.deleteComment(commentId, currentUser)

        return ResponseEntity.ok(mapOf(
            "status" to "success"
        ))
    }
}