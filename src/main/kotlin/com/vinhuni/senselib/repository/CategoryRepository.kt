package com.vinhuni.senselib.repository

import com.vinhuni.senselib.model.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CategoryRepository : JpaRepository<Category, Int> {
    @Query("SELECT c FROM Category c WHERE c.status = 'active' ORDER BY c.categoryName")
    fun findAllActiveCategories(): List<Category>

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.status = 'active' ORDER BY c.categoryName")
    fun findAllActiveParentCategories(): List<Category>

    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId AND c.status = 'active' ORDER BY c.categoryName")
    fun findAllActiveChildCategories(parentId: Int): List<Category>
}