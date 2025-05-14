package com.vinhuni.senselib.service

import com.vinhuni.senselib.model.Category
import com.vinhuni.senselib.repository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryService(private val categoryRepository: CategoryRepository) {

    fun getAllActiveCategories(): List<Category> {
        return categoryRepository.findAllActiveCategories()
    }

    fun getAllParentCategories(): List<Category> {
        return categoryRepository.findAllActiveParentCategories()
    }

    fun getChildCategoriesByParentId(parentId: Int): List<Category> {
        return categoryRepository.findAllActiveChildCategories(parentId)
    }
    fun getCategoryHierarchy(): Map<Int, List<Category>> {
        val parentCategories = getAllParentCategories()
        val childCategoriesMap = mutableMapOf<Int, List<Category>>()

        parentCategories.forEach { parent ->
            parent.id?.let { parentId ->
                childCategoriesMap[parentId] = getChildCategoriesByParentId(parentId)
            }
        }

        return childCategoriesMap
    }
}