package com.vinhuni.senselib.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*

@Embeddable
open class BookAuthorId : Serializable {
    @Column(name = "book_id", nullable = false)
    open var bookId: Int? = null

    @Column(name = "author_id", nullable = false)
    open var authorId: Int? = null
    override fun hashCode(): Int = Objects.hash(bookId, authorId)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as BookAuthorId

        return bookId == other.bookId &&
                authorId == other.authorId
    }

    companion object {
        private const val serialVersionUID = 0L
    }
}