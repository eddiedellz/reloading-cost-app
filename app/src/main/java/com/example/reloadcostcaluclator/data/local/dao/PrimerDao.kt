package com.example.reloadcostcaluclator.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.reloadcostcaluclator.data.local.entity.PrimerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrimerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(primer: PrimerEntity): Long

    @Update
    suspend fun update(primer: PrimerEntity)

    @Delete
    suspend fun delete(primer: PrimerEntity)

    @Query("SELECT * FROM primers ORDER BY name ASC")
    fun getAll(): Flow<List<PrimerEntity>>

    @Query("SELECT * FROM primers WHERE id = :id")
    fun getById(id: Long): Flow<PrimerEntity?>

    @Query("SELECT * FROM primers WHERE lower(name) = lower(:name) LIMIT 1")
    suspend fun getByName(name: String): PrimerEntity?
}
