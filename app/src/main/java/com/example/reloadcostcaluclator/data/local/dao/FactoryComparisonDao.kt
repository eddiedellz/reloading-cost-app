package com.example.reloadcostcaluclator.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.reloadcostcaluclator.data.local.entity.FactoryComparisonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FactoryComparisonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(factoryComparison: FactoryComparisonEntity): Long

    @Update
    suspend fun update(factoryComparison: FactoryComparisonEntity)

    @Delete
    suspend fun delete(factoryComparison: FactoryComparisonEntity)

    @Query("SELECT * FROM factory_comparisons ORDER BY updatedAtEpochMillis DESC")
    fun getAll(): Flow<List<FactoryComparisonEntity>>

    @Query("SELECT * FROM factory_comparisons WHERE id = :id")
    fun getById(id: Long): Flow<FactoryComparisonEntity?>
}
