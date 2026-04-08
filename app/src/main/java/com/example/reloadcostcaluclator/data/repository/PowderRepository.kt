package com.example.reloadcostcaluclator.data.repository

import com.example.reloadcostcaluclator.data.local.dao.PowderDao
import com.example.reloadcostcaluclator.data.local.entity.PowderEntity
import kotlinx.coroutines.flow.Flow

class PowderRepository(
    private val powderDao: PowderDao,
) {
    fun getAll(): Flow<List<PowderEntity>> = powderDao.getAll()

    fun getById(id: Long): Flow<PowderEntity?> = powderDao.getById(id)

    suspend fun insert(powder: PowderEntity): Long = powderDao.insert(powder)

    suspend fun update(powder: PowderEntity) = powderDao.update(powder)

    suspend fun delete(powder: PowderEntity) = powderDao.delete(powder)
}
