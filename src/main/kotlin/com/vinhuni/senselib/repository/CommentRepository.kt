package com.vinhuni.senselib.repository
import com.vinhuni.senselib.model.Comment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : JpaRepository<Comment, Int>{
    fun findByBookIdOrderByCreatedAtDesc(bookId: Int, pageable: Pageable): Page<Comment>
    fun findByBookIdAndParentIsNullOrderByCreatedAtDesc(bookId: Int, pageable: Pageable): Page<Comment>
    fun findByParentIdOrderByCreatedAtAsc(parentId: Int): List<Comment>
    fun countByBookId(bookId: Int): Long
    fun findByParentId(parentId: Int): List<Comment>
    fun findAllByBookIdOrderByCreatedAtDesc(bookId: Int): List<Comment>
}