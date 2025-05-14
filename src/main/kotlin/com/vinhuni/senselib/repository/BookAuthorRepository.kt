package com.vinhuni.senselib.repository

import com.vinhuni.senselib.model.BookAuthor
import com.vinhuni.senselib.model.BookAuthorId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BookAuthorRepository : JpaRepository<BookAuthor, BookAuthorId> {
    fun findByBookId(bookId: Int): List<BookAuthor>
    fun findByIdBookId(bookId: Int): List<BookAuthor>
    fun findByIdAuthorId(authorId: Int): List<BookAuthor>
}