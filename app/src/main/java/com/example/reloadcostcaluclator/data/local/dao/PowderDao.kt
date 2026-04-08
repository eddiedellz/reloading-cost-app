package com.example.reloadcostcaluclator.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.reloadcostcaluclator.data.local.entity.PowderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PowderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(powder: PowderEntity): Long

    @Update
    suspend fun update(powder: PowderEntity)

    @Delete
    suspend fun delete(powder: PowderEntity)

    @Query("SELECT * FROM powders ORDER BY name ASC")
    fun getAll(): Flow<List<PowderEntity>>

    @Query("SELECT * FROM powders WHERE id = :id")
    fun getById(id: Long): Flow<PowderEntity?>

    @Query("SELECT * FROM powders WHERE lower(name) = lower(:name) LIMIT 1")
    suspend fun getByName(name: String): PowderEntity?
}
