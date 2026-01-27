package com.pucetec.fintrack.repositories

import com.pucetec.fintrack.models.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): Optional<User>
}
