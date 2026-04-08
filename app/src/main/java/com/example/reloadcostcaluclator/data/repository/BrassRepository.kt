package com.example.reloadcostcaluclator.data.repository

import com.example.reloadcostcaluclator.data.local.dao.BrassDao
import com.example.reloadcostcaluclator.data.local.entity.BrassEntity
import kotlinx.coroutines.flow.Flow

class BrassRepository(
    private val brassDao: BrassDao,
) {
    fun getAll(): Flow<List<BrassEntity>> = brassDao.getAll()

    fun getById(id: Long): Flow<BrassEntity?> = brassDao.getById(id)

    suspend fun insert(brass: BrassEntity): Long = brassDao.insert(brass)

    suspend fun update(brass: BrassEntity) = brassDao.update(brass)

    suspend fun delete(brass: BrassEntity) = brassDao.delete(brass)
}
