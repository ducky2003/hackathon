package com.vinhuni.senselib.model

import com.vinhuni.senselib.model.User
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.Instant

@Entity
@Table(name = "downloads")
open class Download {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "download_id", nullable = false)
    open var id: Int? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "book_id", nullable = false)
    open var book: Book? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: User? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "download_date")
    open var downloadDate: Instant? = null

    @Column(name = "ip_address", length = 45)
    open var ipAddress: String? = null
    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false
}