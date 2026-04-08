package com.example.reloadcostcaluclator.data.repository

import com.example.reloadcostcaluclator.data.local.dao.BulletDao
import com.example.reloadcostcaluclator.data.local.entity.BulletEntity
import kotlinx.coroutines.flow.Flow

class BulletRepository(
    private val bulletDao: BulletDao,
) {
    fun getAll(): Flow<List<BulletEntity>> = bulletDao.getAll()

    fun getById(id: Long): Flow<BulletEntity?> = bulletDao.getById(id)

    suspend fun insert(bullet: BulletEntity): Long = bulletDao.insert(bullet)

    suspend fun update(bullet: BulletEntity) = bulletDao.update(bullet)

    suspend fun delete(bullet: BulletEntity) = bulletDao.delete(bullet)
}
