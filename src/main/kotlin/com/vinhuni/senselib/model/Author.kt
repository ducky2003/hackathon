package com.vinhuni.senselib

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.time.Instant

@Entity
@Table(name = "authors")
open class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id", nullable = false)
    open var id: Int? = null

    @Column(name = "author_name", nullable = false, length = 100)
    open var authorName: String? = null

    @Lob
    @Column(name = "description")
    open var description: String? = null

    @Column(name = "birth_year")
    open var birthYear: Int? = null

    @Column(name = "death_year")
    open var deathYear: Int? = null

    @Column(name = "avatar")
    open var avatar: String? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    open var createdAt: Instant? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    open var updatedAt: Instant? = null
}