package com.pucetec.fintrack.models.entities

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "categories",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id", "name"])
    ]
)
class Category(

    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(nullable = false)
    var name: String,

    @Column(name = "is_income", nullable = false)
    var isIncome: Boolean,

    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now(),

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var transactions: MutableList<Transaction> = mutableListOf()
)
