package com.pucetec.fintrack.repositories

import com.pucetec.fintrack.models.entities.Category
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CategoryRepository : JpaRepository<Category, UUID> {
    fun findAllByUser_Id(userId: UUID): List<Category>
}
