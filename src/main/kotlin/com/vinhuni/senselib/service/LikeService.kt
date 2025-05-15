package com.vinhuni.senselib.service
import com.vinhuni.senselib.model.Book
import com.vinhuni.senselib.model.Like
import com.vinhuni.senselib.model.User
import com.vinhuni.senselib.repository.LikeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class LikeService @Autowired constructor(
    private val likeRepository: LikeRepository
) {
    fun getUserLikes(user: User): List<Like> {
        return likeRepository.findAllByUserOrderByCreatedAtDesc(user)
    }

    fun isLiked(user: User, book: Book): Boolean {
        return likeRepository.existsByUserAndBook(user, book)
    }

    fun addLike(user: User, book: Book): Like {
        val existingLike = likeRepository.findByUserAndBook(user, book)
        if (existingLike != null) {
            return existingLike
        }

        val like = Like()
        like.user = user
        like.book = book
        like.createdAt = Instant.now()
        return likeRepository.save(like)
    }

    @Transactional
    fun removeLike(user: User, book: Book) {
        likeRepository.deleteByUserAndBook(user, book)
    }
}