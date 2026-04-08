package com.example.reloadcostcaluclator.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "load_recipes",
    foreignKeys = [
        ForeignKey(
            entity = PowderEntity::class,
            parentColumns = ["id"],
            childColumns = ["powderId"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = PrimerEntity::class,
            parentColumns = ["id"],
            childColumns = ["primerId"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = BulletEntity::class,
            parentColumns = ["id"],
            childColumns = ["bulletId"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = BrassEntity::class,
            parentColumns = ["id"],
            childColumns = ["brassId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index(value = ["powderId"]),
        Index(value = ["primerId"]),
        Index(value = ["bulletId"]),
        Index(value = ["brassId"]),
    ],
)
data class LoadRecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val caliber: String,
    val powderId: Long?,
    val chargeWeightGr: Double,
    val primerId: Long?,
    val bulletId: Long?,
    val brassId: Long?,
    val notes: String,
)
