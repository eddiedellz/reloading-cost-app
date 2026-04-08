package com.example.reloadcostcaluclator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "powders")
data class PowderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val pricePaid: Double,
    val containerWeightLb: Double,
    val pricingStrategy: String = ComponentUpdateMode.LATEST_PRICE.name,
)
