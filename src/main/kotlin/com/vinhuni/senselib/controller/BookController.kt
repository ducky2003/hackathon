package com.vinhuni.senselib.controller

import com.vinhuni.senselib.dto.BookDTO
import com.vinhuni.senselib.model.Comment
import com.vinhuni.senselib.repository.BookAuthorRepository
import com.vinhuni.senselib.repository.BookRepository
import com.vinhuni.senselib.service.BookService
import com.vinhuni.senselib.service.CommentService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.security.core.Authentication
import com.vinhuni.senselib.repository.UserRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class BookController(
    private val bookService: BookService,
    private val bookAuthorRepository: BookAuthorRepository,
    private val bookRepository: BookRepository,
    private val commentService: CommentService,
    private val userRepository: UserRepository  // Add this

) {

    @GetMapping("/document-detail/{id}")
    fun getBookDetails(@PathVariable id: Int, model: Model, authentication: Authentication?): String {
        // Increment view count
        try{
            bookService.incrementViewCount(id)

            val book = bookService.getBookById(id)
            val authors = bookAuthorRepository.findByBookId(id)
                .mapNotNull { it.author?.authorName }

            val downloadCount = bookRepository.countDownloadsByBookId(id)
            val rating = bookRepository.getAverageRatingByBookId(id) ?: 0.0
            val categoryId = book.category?.id
            val relatedBooks = if (categoryId != null && categoryId > 0) {
                try {
                    // Convert List<Book> to List<BookDTO>
                    bookRepository.findRandomBooksInSameCategory(categoryId, id, 5).map { relatedBook ->
                        val relatedAuthors = bookAuthorRepository.findByBookId(relatedBook.id!!)
                            .mapNotNull { it.author?.authorName }
                        val relatedDownloadCount = bookRepository.countDownloadsByBookId(relatedBook.id!!)
                        val relatedRating = bookRepository.getAverageRatingByBookId(relatedBook.id!!) ?: 0.0

                        BookDTO(
                            id = relatedBook.id,
                            title = relatedBook.title,
                            authors = relatedAuthors,
                            publisherName = relatedBook.publisher?.publisherName,
                            coverImage = relatedBook.coverImage,
                            createdAt = relatedBook.createdAt,
                            viewCount = relatedBook.viewCount,
                            downloadCount = relatedDownloadCount,
                            rating = relatedRating
                        )
                    }
                } catch (e: Exception) {
                    println("Error getting related books: ${e.message}")
                    emptyList()
                }
            } else {
                emptyList()
            }
            if (authentication != null && authentication.isAuthenticated) {
                val username = authentication.name
                val currentUser = userRepository.findByUsername(username)
                model.addAttribute("currentUser", currentUser)
            }
            val pageable = PageRequest.of(0, 50, Sort.by("createdAt").descending())
            val comments = commentService.getCommentsByBookId(id, pageable)
            println("Found ${comments.totalElements} comments for book $id")

            val commentReplies = HashMap<Int, List<Comment>>()
            comments.forEach { comment ->
                comment.id?.let { commentId ->
                    commentReplies[commentId] = commentService.getRepliesForComment(commentId)
                }
            }
            val commentCount = commentService.countCommentsByBookId(id)
            model.addAttribute("book", book)
            model.addAttribute("authors", authors)
            model.addAttribute("downloadCount", downloadCount)
            model.addAttribute("rating", rating)
            model.addAttribute("relatedBooks", relatedBooks)
            model.addAttribute("comments", comments)
            model.addAttribute("commentReplies", commentReplies)
            model.addAttribute("commentCount", commentCount)
            return "document-detail"
        } catch (e: Exception) {
            // Handle the exception (e.g., log it, show an error page, etc.)
            e.printStackTrace()
            return "error"
        }
    }
    @GetMapping("/book/{id}")
    fun redirectToBookDetail(@PathVariable id: Int): String {
        return "redirect:/document-detail/$id"
    }
//    fun showBookDetail(@PathVariable id: Int, model: Model, authentication: Authentication?): String {
//        try {
//            val book = bookService.getBookById(id)
//            bookService.incrementViewCount(id) // Call only once
//
//            val categoryId = book.category?.id
//            val relatedBooks = if (categoryId != null && categoryId > 0) {
//                bookService.getRandomRelatedBooks(categoryId, id)
//            } else {
//                emptyList()
//            }
//            if (authentication != null && authentication.isAuthenticated) {
//                val username = authentication.name
//                val currentUser = userRepository.findByUsername(username)
//                model.addAttribute("currentUser", currentUser)
//            }
//            // Debug info
//            println("RelatedBooks có ${relatedBooks.size} items")
//            if (relatedBooks.isNotEmpty()) {
//                println("Tài liệu đầu tiên: ${relatedBooks[0].title}")
//            }
//
//            // Set model attributes
//            val authors = bookAuthorRepository.findByBookId(id)
//                .mapNotNull { it.author?.authorName }
//
//            val downloadCount = bookRepository.countDownloadsByBookId(id)
//            val rating = bookRepository.getAverageRatingByBookId(id) ?: 0.0
//            val pageable = PageRequest.of(0, 50, Sort.by("createdAt").descending())
//            val comments = commentService.getCommentsByBookId(id, pageable)
//            println("Found ${comments.totalElements} comments for book $id")
//
//            val commentReplies = HashMap<Int, List<Comment>>()
//            comments.forEach { comment ->
//                comment.id?.let { commentId ->
//                    println("Comment ID: ${comment.id}, Text: ${comment.commentText}")
//
//                    commentReplies[commentId] = commentService.getRepliesForComment(commentId)
//                }
//            }
//            val commentCount = commentService.countCommentsByBookId(id)
//
//            model.addAttribute("book", book)
//            model.addAttribute("authors", authors)
//            model.addAttribute("downloadCount", downloadCount)
//            model.addAttribute("rating", rating)
//            model.addAttribute("relatedBooks", relatedBooks)
//            model.addAttribute("comments", comments)
//            model.addAttribute("commentReplies", commentReplies)
//            model.addAttribute("commentCount", commentCount)
//            return "document-detail"
//        } catch (e: Exception) {
//            e.printStackTrace() // Log the exception
//            return "redirect:/"
//        }
//    }
}