package com.vinhuni.senselib

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.Instant

@Entity
@Table(name = "categories")
open class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    open var id: Int? = null

    @Column(name = "category_name", nullable = false, length = 100)
    open var categoryName: String? = null

    @ColumnDefault("'active'")
    @Lob
    @Column(name = "status")
    open var status: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "parent_id")
    open var parent: Category? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    open var createdAt: Instant? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    open var updatedAt: Instant? = null
}