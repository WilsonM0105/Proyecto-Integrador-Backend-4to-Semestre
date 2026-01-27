package com.pucetec.fintrack.models.entities

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users")
class User(

    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "full_name", nullable = false)
    var fullName: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now(),

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var categories: MutableList<Category> = mutableListOf(),

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var transactions: MutableList<Transaction> = mutableListOf()
)
