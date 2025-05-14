package com.vinhuni.senselib.model

import com.vinhuni.senselib.model.User
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.Instant

@Entity
@Table(name = "books")
open class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    var id: Int = 0

    @Column(name = "title", nullable = false)
    open var title: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "category_id")
    open var category: Category? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "publisher_id", nullable = false)
    open var publisher: Publisher? = null

    @Column(name = "publication_year")
    open var publicationYear: Int? = null

    @Column(name = "isbn", length = 20)
    open var isbn: String? = null

    @Lob
    @Column(name = "description")
    open var description: String? = null

    @Column(name = "cover_image")
    open var coverImage: String? = null

    @Column(name = "file_path", nullable = false)
    open var filePath: String? = null

    @Column(name = "file_size")
    open var fileSize: Int? = null

    @ColumnDefault("'available'")
    @Lob
    @Column(name = "status")
    open var status: String? = null

    @Column(name = "page_count")
    open var pageCount: Int? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    open var createdAt: Instant? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    open var updatedAt: Instant? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    open var createdBy: User? = null

    @Column(name = "view_count")
    @ColumnDefault("0")
    open var viewCount: Int = 0
}