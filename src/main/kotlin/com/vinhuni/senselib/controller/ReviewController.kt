package com.vinhuni.senselib.controller
import com.vinhuni.senselib.model.Review
import com.vinhuni.senselib.repository.BookRepository
import com.vinhuni.senselib.repository.ReviewRepository
import com.vinhuni.senselib.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/api/reviews")
class ReviewController (
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
    private val bookRepository: BookRepository
){
    @GetMapping("/user-rating")
    fun getUserRating(
        @RequestParam bookId: Int,
        authentication: Authentication
    ): ResponseEntity<Map<String, Any?>> {  // Changed return type to allow nullable values
        val username = authentication.name
        val user = userRepository.findByUsername(username) ?: return ResponseEntity.ok(
            mapOf("hasRating" to false)
        )

        val review = reviewRepository.findByBookIdAndUserId(bookId, user.id!!)
        return if (review != null) {
            ResponseEntity.ok(
                mapOf(
                    "hasRating" to true,
                    "rating" to review.rating,
                    "reviewText" to (review.reviewText ?: "")
                )
            )
        } else {
            ResponseEntity.ok(mapOf("hasRating" to false))
        }
    }

    @PostMapping("/submit")
    fun submitReview(
        @RequestBody reviewData: Map<String, Any>,
        authentication: Authentication
    ): ResponseEntity<Map<String, Any>> {
        val username = authentication.name
        val user = userRepository.findByUsername(username)
            ?: return ResponseEntity.badRequest().body(mapOf("status" to "error", "message" to "User not found"))

        val bookId = (reviewData["bookId"] as String).toInt()
        val rating = (reviewData["rating"] as String).toByte()
        val reviewText = reviewData["reviewText"] as String?

        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body(
                mapOf("status" to "error", "message" to "Rating must be between 1 and 5")
            )
        }

        val book = bookRepository.findById(bookId).orElse(null)
            ?: return ResponseEntity.badRequest().body(
                mapOf("status" to "error", "message" to "Book not found")
            )

        // Check if user already has a review for this book
        val existingReview = reviewRepository.findByBookIdAndUserId(bookId, user.id!!)

        if (existingReview != null) {
            // Update existing review
            existingReview.rating = rating
            existingReview.reviewText = reviewText
            existingReview.updatedAt = Instant.now()
            reviewRepository.save(existingReview)
        } else {
            // Create new review
            val review = Review().apply {
                this.book = book
                this.user = user
                this.rating = rating
                this.reviewText = reviewText
                this.createdAt = Instant.now()
                this.updatedAt = Instant.now()
            }
            reviewRepository.save(review)
        }

        // Calculate new average rating
        val newAverage = bookRepository.getAverageRatingByBookId(bookId) ?: 0.0

        return ResponseEntity.ok(mapOf(
            "status" to "success",
            "newAverage" to String.format("%.1f", newAverage)
        ))
    }
}