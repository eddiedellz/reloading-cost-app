package com.example.reloadcostcaluclator.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.reloadcostcaluclator.data.local.entity.BulletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BulletDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bullet: BulletEntity): Long

    @Update
    suspend fun update(bullet: BulletEntity)

    @Delete
    suspend fun delete(bullet: BulletEntity)

    @Query("SELECT * FROM bullets ORDER BY name ASC")
    fun getAll(): Flow<List<BulletEntity>>

    @Query("SELECT * FROM bullets WHERE id = :id")
    fun getById(id: Long): Flow<BulletEntity?>

    @Query("SELECT * FROM bullets WHERE lower(name) = lower(:name) LIMIT 1")
    suspend fun getByName(name: String): BulletEntity?
}
