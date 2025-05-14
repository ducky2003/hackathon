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
    @Query("SELECT DISTINCT b FROM Book b WHERE b.category.id = :categoryId AND b.status = 'available' ORDER BY b.createdAt DESC")
    fun findByCategoryId(@Param("categoryId") categoryId: Int, pageable: Pageable): Page<Book>

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
    fun getAverageRatingByBookId(bookId: Int): Double?

    @Query("SELECT DISTINCT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND b.status = 'available' ORDER BY b.createdAt DESC")
    fun findByTitleContaining(@Param("keyword") keyword: String, pageable: Pageable): Page<Book>

    @Query("SELECT DISTINCT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND b.category.id = :categoryId AND b.status = 'available' ORDER BY b.createdAt DESC")
    fun findByTitleContainingAndCategoryId(@Param("keyword") keyword: String, @Param("categoryId") categoryId: Int, pageable: Pageable): Page<Book>

    @Query(value = "SELECT * FROM books WHERE category_id = :categoryId AND id != :bookId AND status = 'available' ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    fun findRandomBooksByCategoryIdAndNotBookId(
        @Param("categoryId") categoryId: Int,
        @Param("bookId") bookId: Int,
        @Param("limit") limit: Int
    ): List<Book>
}