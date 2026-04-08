package com.example.reloadcostcaluclator.data.repository

import com.example.reloadcostcaluclator.data.local.dao.PrimerDao
import com.example.reloadcostcaluclator.data.local.entity.PrimerEntity
import kotlinx.coroutines.flow.Flow

class PrimerRepository(
    private val primerDao: PrimerDao,
) {
    fun getAll(): Flow<List<PrimerEntity>> = primerDao.getAll()

    fun getById(id: Long): Flow<PrimerEntity?> = primerDao.getById(id)

    suspend fun insert(primer: PrimerEntity): Long = primerDao.insert(primer)

    suspend fun update(primer: PrimerEntity) = primerDao.update(primer)

    suspend fun delete(primer: PrimerEntity) = primerDao.delete(primer)
}
