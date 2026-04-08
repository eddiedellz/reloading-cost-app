package com.example.reloadcostcaluclator.data.repository

import com.example.reloadcostcaluclator.data.local.dao.LoadRecipeDao
import com.example.reloadcostcaluclator.data.local.entity.LoadRecipeEntity
import kotlinx.coroutines.flow.Flow

class LoadRecipeRepository(
    private val loadRecipeDao: LoadRecipeDao,
) {
    fun getAll(): Flow<List<LoadRecipeEntity>> = loadRecipeDao.getAll()

    fun getById(id: Long): Flow<LoadRecipeEntity?> = loadRecipeDao.getById(id)

    suspend fun insert(loadRecipe: LoadRecipeEntity): Long = loadRecipeDao.insert(loadRecipe)

    suspend fun update(loadRecipe: LoadRecipeEntity) = loadRecipeDao.update(loadRecipe)

    suspend fun delete(loadRecipe: LoadRecipeEntity) = loadRecipeDao.delete(loadRecipe)
}
