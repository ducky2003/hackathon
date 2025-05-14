package com.vinhuni.senselib.repository

import com.vinhuni.senselib.model.BookAuthor
import com.vinhuni.senselib.model.BookAuthorId
import org.springframework.data.jpa.repository.JpaRepository

interface BookAuthorRepository : JpaRepository<BookAuthor, BookAuthorId> {
    fun findByBookId(bookId: Int): List<BookAuthor>
}