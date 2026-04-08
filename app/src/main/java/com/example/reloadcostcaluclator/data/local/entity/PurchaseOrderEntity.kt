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
    val orderTotal: Double,
    val extraChargesTotal: Double,
    val subtotal: Double,
    val grandTotal: Double,
)
