package com.example.reloadcostcaluclator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchase_orders")
data class PurchaseOrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val purchaseDateEpochMillis: Long,
    val extraChargeMode: String,
    val allocationMethod: String,
    val totalCents: Int,
    val extraChargesCents: Int,
    val subtotalCents: Int,
)
