package com.example.reloadcostcaluclator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bullets")
data class BulletEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val grain: Int?,
    val bulletType: String?,
    val pricePaid: Double,
    val quantity: Int,
)
