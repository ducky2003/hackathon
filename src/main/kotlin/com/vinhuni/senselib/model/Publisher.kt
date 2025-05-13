package com.vinhuni.senselib

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.time.Instant

@Entity
@Table(name = "publishers")
open class Publisher {
    @Id
    @Column(name = "publisher_id", nullable = false)
    open var id: Int? = null

    @Column(name = "publisher_name", nullable = false, length = 100)
    open var publisherName: String? = null

    @Lob
    @Column(name = "address")
    open var address: String? = null

    @Column(name = "phone", length = 20)
    open var phone: String? = null

    @Column(name = "email", length = 100)
    open var email: String? = null

    @Column(name = "website")
    open var website: String? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    open var createdAt: Instant? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    open var updatedAt: Instant? = null
}