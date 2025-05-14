package com.vinhuni.senselib.controller

import com.vinhuni.senselib.repository.BookAuthorRepository
import com.vinhuni.senselib.repository.BookRepository
import com.vinhuni.senselib.service.BookService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class BookController(
    private val bookService: BookService,
    private val bookAuthorRepository: BookAuthorRepository,
    private val bookRepository: BookRepository
) {

    @GetMapping("/document-detail/{id}")
    fun getBookDetails(@PathVariable id: Int, model: Model): String {
        // Increment view count
        try{
            bookService.incrementViewCount(id)

            // Get the book and add to model
            val book = bookService.getBookById(id)
            val authors = bookAuthorRepository.findByBookId(id)
                .mapNotNull { it.author?.authorName }

            val downloadCount = bookRepository.countDownloadsByBookId(id)
            val rating = bookRepository.getAverageRatingByBookId(id) ?: 0.0
            model.addAttribute("book", book)
            model.addAttribute("authors", authors)
            model.addAttribute("downloadCount", downloadCount)
            model.addAttribute("rating", rating)
            return "document-detail"
        } catch (e: Exception) {
            // Handle the exception (e.g., log it, show an error page, etc.)
            e.printStackTrace()
            return "error"
        }
    }
}