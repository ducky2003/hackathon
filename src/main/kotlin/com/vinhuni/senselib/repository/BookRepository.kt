package com.vinhuni.senselib.repository

import com.vinhuni.senselib.model.Book
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BookRepository : JpaRepository<Book, Int> {
    fun findAllByOrderByCreatedAtDesc(pageable: Pageable): Page<Book>

    @Query("SELECT COUNT(d) FROM Download d WHERE d.book.id = :bookId")
    fun countDownloadsByBookId(bookId: Int): Int
    @Query("SELECT DISTINCT b FROM Book b JOIN b.category c WHERE c.id IN :categoryIds AND b.status = 'active' ORDER BY b.createdAt DESC")
    fun findByCategories(@Param("categoryIds") categoryIds: List<Int>, pageable: Pageable): Page<Book>
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
    fun getAverageRatingByBookId(bookId: Int): Double?
    @Query("SELECT DISTINCT b FROM Book b WHERE b.category.id = :categoryId AND b.status = 'active' ORDER BY b.createdAt DESC")
    fun findByCategoryId(@Param("categoryId") categoryId: Int, pageable: Pageable): Page<Book>
}