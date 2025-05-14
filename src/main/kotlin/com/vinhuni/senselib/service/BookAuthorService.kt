package com.vinhuni.senselib.service

import com.vinhuni.senselib.model.BookAuthor
import com.vinhuni.senselib.repository.BookAuthorRepository
import org.springframework.stereotype.Service

@Service
class BookAuthorService(private val bookAuthorRepository: BookAuthorRepository) {

    fun saveBookAuthor(bookAuthor: BookAuthor): BookAuthor {
        return bookAuthorRepository.save(bookAuthor)
    }

    fun getBookAuthorsByBookId(bookId: Int): List<BookAuthor> {
        return bookAuthorRepository.findByIdBookId(bookId)
    }

    fun getBookAuthorsByAuthorId(authorId: Int): List<BookAuthor> {
        return bookAuthorRepository.findByIdAuthorId(authorId)
    }
}