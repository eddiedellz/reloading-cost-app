package com.example.reloadcostcaluclator.data.repository

import com.example.reloadcostcaluclator.data.local.dao.FactoryComparisonDao
import com.example.reloadcostcaluclator.data.local.entity.FactoryComparisonEntity
import kotlinx.coroutines.flow.Flow

class FactoryComparisonRepository(
    private val dao: FactoryComparisonDao,
) {
    fun getAll(): Flow<List<FactoryComparisonEntity>> = dao.getAll()

    fun getById(id: Long): Flow<FactoryComparisonEntity?> = dao.getById(id)

    suspend fun insert(factoryComparison: FactoryComparisonEntity): Long = dao.insert(factoryComparison)

    suspend fun update(factoryComparison: FactoryComparisonEntity) = dao.update(factoryComparison)

    suspend fun delete(factoryComparison: FactoryComparisonEntity) = dao.delete(factoryComparison)
}
