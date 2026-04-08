package com.example.reloadcostcaluclator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brass")
data class BrassEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val pricePaid: Double,
    val quantity: Int,
    val reloadCount: Int,
)
