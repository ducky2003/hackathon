package com.vinhuni.senselib.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "users")
open class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    open var id: Int? = null

    @Column(name = "username", nullable = false, length = 50)
    open var username: String? = null

    @Column(name = "password", nullable = false)
    open var password: String? = null

    @Column(name = "full_name", nullable = false, length = 100)
    open var fullName: String? = null

    @Column(name = "date_of_birth")
    open var dateOfBirth: LocalDate? = null

    @Lob
    @Column(name = "address")
    open var address: String? = null

    @Column(name = "phone", length = 20)
    open var phone: String? = null

    @Column(name = "email", nullable = false, length = 100)
    open var email: String? = null

    @Lob
    @Column(name = "description")
    open var description: String? = null

    @Column(name = "avatar")
    open var avatar: String? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    open var role: Role? = null

    @ColumnDefault("'active'")
    @Lob
    @Column(name = "account_status")
    open var accountStatus: String? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    open var createdAt: Instant? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    open var updatedAt: Instant? = null
}