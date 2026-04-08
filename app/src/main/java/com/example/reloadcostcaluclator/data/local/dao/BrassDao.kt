package com.example.reloadcostcaluclator.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.reloadcostcaluclator.data.local.entity.BrassEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BrassDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(brass: BrassEntity): Long

    @Update
    suspend fun update(brass: BrassEntity)

    @Delete
    suspend fun delete(brass: BrassEntity)

    @Query("SELECT * FROM brass ORDER BY name ASC")
    fun getAll(): Flow<List<BrassEntity>>

    @Query("SELECT * FROM brass WHERE id = :id")
    fun getById(id: Long): Flow<BrassEntity?>
}
