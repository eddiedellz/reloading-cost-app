package com.example.reloadcostcaluclator.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "purchase_order_items",
    foreignKeys = [
        ForeignKey(
            entity = PurchaseOrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("orderId")],
)
data class PurchaseOrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: Long,
    val componentType: String,
    val itemName: String,
    val unitPrice: Double,
    val packageQuantity: Double,
    val purchaseQuantity: Double,
    val lineSubtotal: Double,
    val allocatedExtraCharge: Double,
    val originalUnitCost: Double,
    val adjustedUnitCost: Double,
    val adjustedLineTotal: Double,
    val landedCost: Double,
)
