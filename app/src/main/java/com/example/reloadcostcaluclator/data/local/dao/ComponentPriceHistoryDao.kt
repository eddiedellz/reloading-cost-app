package com.example.reloadcostcaluclator.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reloadcostcaluclator.data.local.entity.ComponentPriceHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ComponentPriceHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ComponentPriceHistoryEntity>)

    @Query(
        """
        SELECT * FROM component_price_history
        WHERE componentType = :componentType AND componentId = :componentId
        ORDER BY purchaseDateEpochMillis DESC, id DESC
        """,
    )
    fun getByComponent(componentType: String, componentId: Long): Flow<List<ComponentPriceHistoryEntity>>

    @Query(
        """
        SELECT * FROM component_price_history
        WHERE componentType = :componentType AND lower(componentName) = lower(:componentName)
        ORDER BY purchaseDateEpochMillis ASC, id ASC
        """,
    )
    suspend fun getByTypeAndName(componentType: String, componentName: String): List<ComponentPriceHistoryEntity>
}
