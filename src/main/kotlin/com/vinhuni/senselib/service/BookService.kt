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

    fun getPaginatedBooksByCategory(categoryId: Int, page: Int, size: Int): Page<BookDTO> {
        val pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val books = bookRepository.findByCategoryId(categoryId, pageable)
        println("Filtering by category ID: $categoryId")
        println("Found ${books.totalElements} books in category $categoryId")

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
    fun searchBooks(keyword: String, page: Int, size: Int): Page<BookDTO> {
        val pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val books = bookRepository.findByTitleContaining(keyword, pageable)
        println("Searching for books with keyword: $keyword")
        println("Found ${books.totalElements} books matching the search query")

        return mapBooksToDTO(books)
    }

    fun searchBooksByCategory(keyword: String, categoryId: Int, page: Int, size: Int): Page<BookDTO> {
        val pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val books = bookRepository.findByTitleContainingAndCategoryId(keyword, categoryId, pageable)
        println("Searching for books with keyword: $keyword and category ID: $categoryId")
        println("Found ${books.totalElements} books matching both criteria")

        return mapBooksToDTO(books)
    }
    fun saveBook(book: Book): Book {
        return bookRepository.save(book)
    }

    private fun mapBooksToDTO(books: Page<Book>): Page<BookDTO> {
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
}