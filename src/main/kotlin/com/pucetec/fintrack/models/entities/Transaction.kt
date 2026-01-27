package com.pucetec.fintrack.models.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "transactions")
class Transaction(

    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: TransactionType,

    @Column(nullable = false, precision = 12, scale = 2)
    var amount: BigDecimal,

    @Column
    var description: String? = null,

    @Column(name = "trx_date", nullable = false)
    var trxDate: LocalDate,

    @Column(nullable = true)
    var note: String? = null,

    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now()
)
