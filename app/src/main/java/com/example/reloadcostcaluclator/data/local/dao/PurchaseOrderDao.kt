package com.example.reloadcostcaluclator.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.reloadcostcaluclator.data.local.entity.PurchaseOrderEntity
import com.example.reloadcostcaluclator.data.local.entity.PurchaseOrderItemEntity

@Dao
interface PurchaseOrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: PurchaseOrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<PurchaseOrderItemEntity>): List<Long>
}
