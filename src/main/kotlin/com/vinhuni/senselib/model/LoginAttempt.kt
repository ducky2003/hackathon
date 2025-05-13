package com.vinhuni.senselib.model

import com.vinhuni.senselib.model.User
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.time.Instant

@Entity
@Table(name = "login_attempts")
open class LoginAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attempt_id", nullable = false)
    open var id: Int? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: User? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "login_time")
    open var loginTime: Instant? = null

    @Column(name = "ip_address", length = 45)
    open var ipAddress: String? = null

    @Lob
    @Column(name = "status", nullable = false)
    open var status: String? = null

    @Column(name = "lock_until")
    open var lockUntil: Instant? = null
}