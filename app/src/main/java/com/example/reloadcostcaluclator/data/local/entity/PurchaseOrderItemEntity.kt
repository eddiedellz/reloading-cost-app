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
    val unitPriceCents: Int,
    val packageQuantity: Double,
    val purchaseQuantity: Double,
    val lineSubtotalCents: Int,
    val allocatedExtraChargeCents: Int,
    val originalUnitCostCents: Int,
    val adjustedUnitCostCents: Int,
    val adjustedLineTotalCents: Int,
    val landedCostCents: Int,
)
