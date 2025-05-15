package com.vinhuni.senselib.repository

import com.vinhuni.senselib.model.Review
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ReviewRepository : JpaRepository<Review, Int> {
    fun findByBookId(bookId: Int): List<Review>

    @Query("SELECT r FROM Review r WHERE r.book.id = :bookId AND r.user.id = :userId")
    fun findByBookIdAndUserId(bookId: Int, userId: Int): Review?

    @Query("SELECT COUNT(r) FROM Review r WHERE r.book.id = :bookId")
    fun countByBookId(bookId: Int): Long
}