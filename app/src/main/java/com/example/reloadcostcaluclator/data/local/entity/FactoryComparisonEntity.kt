package com.example.reloadcostcaluclator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "factory_comparisons")
data class FactoryComparisonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val brand: String,
    val productName: String,
    val caliber: String,
    val grain: Int,
    val bulletType: String?,
    val boxQuantity: Int,
    val totalPrice: Double,
    val costPerRound: Double,
    val notes: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)
