package com.vinhuni.senselib.service

import com.vinhuni.senselib.dto.BookDTO
import com.vinhuni.senselib.model.Book
import com.vinhuni.senselib.repository.BookAuthorRepository
import com.vinhuni.senselib.repository.BookRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val bookAuthorRepository: BookAuthorRepository
) {
    fun getPaginatedBooks(page: Int, size: Int): Page<BookDTO> {
        val pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val books = bookRepository.findAllByOrderByCreatedAtDesc(pageable)

        return books.map { book ->
            val authors = bookAuthorRepository.findByBookId(book.id!!)
                .mapNotNull { it.author?.authorName }

            val downloadCount = bookRepository.countDownloadsByBookId(book.id!!)
            val rating = bookRepository.getAverageRatingByBookId(book.id!!) ?: 0.0

            BookDTO(
                id = book.id,
                title = book.title,
                authors = authors,
                publisherName = book.publisher?.publisherName,
                coverImage = book.coverImage,
                createdAt = book.createdAt,
                viewCount = book.viewCount,
                downloadCount = downloadCount,
                rating = rating
            )
        }
    }
    fun getPaginatedBooksByCategories(categoryIds: List<Int>, page: Int, size: Int): Page<BookDTO> {
        val pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val books = bookRepository.findByCategories(categoryIds, pageable)

        return books.map { book ->
            val authors = bookAuthorRepository.findByBookId(book.id!!)
                .mapNotNull { it.author?.authorName }

            val downloadCount = bookRepository.countDownloadsByBookId(book.id!!)
            val rating = bookRepository.getAverageRatingByBookId(book.id!!) ?: 0.0

            BookDTO(
                id = book.id,
                title = book.title,
                authors = authors,
                publisherName = book.publisher?.publisherName,
                coverImage = book.coverImage,
                createdAt = book.createdAt,
                viewCount = book.viewCount,
                downloadCount = downloadCount,
                rating = rating
            )
        }
    }
    fun getPaginatedBooksByCategory(categoryId: Int, page: Int, size: Int): Page<BookDTO> {
        val pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val books = bookRepository.findByCategoryId(categoryId, pageable)

        return books.map { book ->
            val authors = bookAuthorRepository.findByBookId(book.id!!)
                .mapNotNull { it.author?.authorName }

            val downloadCount = bookRepository.countDownloadsByBookId(book.id!!)
            val rating = bookRepository.getAverageRatingByBookId(book.id!!) ?: 0.0

            BookDTO(
                id = book.id,
                title = book.title,
                authors = authors,
                publisherName = book.publisher?.publisherName,
                coverImage = book.coverImage,
                createdAt = book.createdAt,
                viewCount = book.viewCount,
                downloadCount = downloadCount,
                rating = rating
            )
        }
    }
    @Transactional
    fun incrementViewCount(bookId: Int) {
        val book = bookRepository.findById(bookId).orElseThrow {
            NoSuchElementException("Book not found with ID: $bookId")
        }
        book.viewCount += 1
        bookRepository.save(book)
    }
    fun getBookById(id: Int): Book {
        return bookRepository.findById(id).orElseThrow {
            NoSuchElementException("Book not found with ID: $id")
        }
    }
}