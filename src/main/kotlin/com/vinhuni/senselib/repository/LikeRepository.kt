package com.vinhuni.senselib.repository
import com.vinhuni.senselib.model.Book
import com.vinhuni.senselib.model.Like
import com.vinhuni.senselib.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeRepository : JpaRepository<Like, Int> {
    fun findByUserAndBook(user: User, book: Book): Like?
    fun findAllByUserOrderByCreatedAtDesc(user: User): List<Like>
    fun existsByUserAndBook(user: User, book: Book): Boolean
    fun deleteByUserAndBook(user: User, book: Book)
}