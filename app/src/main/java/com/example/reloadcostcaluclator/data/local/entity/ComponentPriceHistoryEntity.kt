package com.example.reloadcostcaluclator.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "component_price_history",
    indices = [Index(value = ["componentType", "componentId"]), Index(value = ["componentType", "componentName"])],
)
data class ComponentPriceHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val componentType: String,
    val componentId: Long?,
    val componentName: String,
    val purchaseDateEpochMillis: Long,
    val orderId: Long,
    val orderItemId: Long,
    val quantity: Double,
    val landedCost: Double,
)
