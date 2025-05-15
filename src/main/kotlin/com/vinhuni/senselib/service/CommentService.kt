package com.vinhuni.senselib.service
import com.vinhuni.senselib.model.Comment
import com.vinhuni.senselib.model.User
import com.vinhuni.senselib.repository.BookRepository
import com.vinhuni.senselib.repository.CommentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val bookRepository: BookRepository
) {
    fun getCommentsByBookId(bookId: Int, pageable: Pageable): Page<Comment> {
        return commentRepository.findByBookIdAndParentIsNullOrderByCreatedAtDesc(bookId, pageable)
    }

    fun getRepliesForComment(commentId: Int): List<Comment> {
        return commentRepository.findByParentIdOrderByCreatedAtAsc(commentId)
    }

    fun countCommentsByBookId(bookId: Int): Long {
        return commentRepository.countByBookId(bookId)
    }

    fun getCommentsByBookId(bookId: Int): List<Comment> {
        return commentRepository.findAllByBookIdOrderByCreatedAtDesc(bookId)
    }
    @Transactional
    fun addComment(bookId: Int, text: String, user: User, parentId: Int? = null): Comment {
        val book = bookRepository.findById(bookId)
            .orElseThrow { IllegalArgumentException("Book not found with id: $bookId") }

        val parentComment = if (parentId != null) {
            commentRepository.findById(parentId)
                .orElseThrow { IllegalArgumentException("Parent comment not found with id: $parentId") }
        } else null


        val comment = Comment().apply {
            this.commentText = text
            this.book = book
            this.user = user
            this.parent = parentComment
            this.createdAt = Instant.now()
        }

        return commentRepository.save(comment)
    }

    @Transactional
    fun updateComment(commentId: Int, text: String, currentUser: User): Comment {
        val comment = commentRepository.findById(commentId)
            .orElseThrow { IllegalArgumentException("Comment not found with id: $commentId") }

        // Check if user owns the comment
        if (comment.user?.id != currentUser.id) {
            throw SecurityException("User not authorized to edit this comment")
        }

        comment.commentText = text
        comment.updatedAt = Instant.now()

        return commentRepository.save(comment)
    }

    @Transactional
    fun deleteComment(commentId: Int, currentUser: User) {
        val comment = commentRepository.findById(commentId)
            .orElseThrow { IllegalArgumentException("Comment not found with id: $commentId") }

        if (comment.user?.id != currentUser.id) {
            throw SecurityException("User not authorized to delete this comment")
        }

        // Delete related replies first
        val replies = commentRepository.findByParentId(commentId)
        commentRepository.deleteAll(replies)

        // Then delete the comment itself
        commentRepository.delete(comment)
    }
}