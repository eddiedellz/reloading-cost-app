package com.example.reloadcostcaluclator.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.reloadcostcaluclator.data.local.dao.BrassDao
import com.example.reloadcostcaluclator.data.local.dao.BulletDao
import com.example.reloadcostcaluclator.data.local.dao.LoadRecipeDao
import com.example.reloadcostcaluclator.data.local.dao.PowderDao
import com.example.reloadcostcaluclator.data.local.dao.PrimerDao
import com.example.reloadcostcaluclator.data.local.entity.BrassEntity
import com.example.reloadcostcaluclator.data.local.entity.BulletEntity
import com.example.reloadcostcaluclator.data.local.entity.LoadRecipeEntity
import com.example.reloadcostcaluclator.data.local.entity.PowderEntity
import com.example.reloadcostcaluclator.data.local.entity.PrimerEntity

@Database(
    entities = [
        PowderEntity::class,
        PrimerEntity::class,
        BulletEntity::class,
        BrassEntity::class,
        LoadRecipeEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class ReloadingDatabase : RoomDatabase() {
    abstract fun powderDao(): PowderDao
    abstract fun primerDao(): PrimerDao
    abstract fun bulletDao(): BulletDao
    abstract fun brassDao(): BrassDao
    abstract fun loadRecipeDao(): LoadRecipeDao
}
