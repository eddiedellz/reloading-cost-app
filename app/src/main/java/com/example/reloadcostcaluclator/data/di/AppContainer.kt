package com.example.reloadcostcaluclator.data.di

import android.content.Context
import androidx.room.Room
import com.example.reloadcostcaluclator.data.local.db.ReloadingDatabase
import com.example.reloadcostcaluclator.data.local.db.ReloadingDatabaseMigrations
import com.example.reloadcostcaluclator.data.reference.FactoryAmmoReferenceRepository
import com.example.reloadcostcaluclator.data.repository.BrassRepository
import com.example.reloadcostcaluclator.data.repository.BulletRepository
import com.example.reloadcostcaluclator.data.repository.LoadRecipeRepository
import com.example.reloadcostcaluclator.data.repository.PowderRepository
import com.example.reloadcostcaluclator.data.repository.PrimerRepository
import com.example.reloadcostcaluclator.data.repository.PurchaseOrderRepository

class AppContainer(context: Context) {
    private val database: ReloadingDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            ReloadingDatabase::class.java,
            "reloading_database",
        )
            .addMigrations(
                ReloadingDatabaseMigrations.MIGRATION_1_2,
                ReloadingDatabaseMigrations.MIGRATION_2_3,
                ReloadingDatabaseMigrations.MIGRATION_3_4,
            )
            .build()
    }

    val powderRepository: PowderRepository by lazy {
        PowderRepository(database.powderDao())
    }

    val primerRepository: PrimerRepository by lazy {
        PrimerRepository(database.primerDao())
    }

    val bulletRepository: BulletRepository by lazy {
        BulletRepository(database.bulletDao())
    }

    val brassRepository: BrassRepository by lazy {
        BrassRepository(database.brassDao())
    }

    val loadRecipeRepository: LoadRecipeRepository by lazy {
        LoadRecipeRepository(database.loadRecipeDao())
    }

    val purchaseOrderRepository: PurchaseOrderRepository by lazy {
        PurchaseOrderRepository(
            database = database,
            purchaseOrderDao = database.purchaseOrderDao(),
            priceHistoryDao = database.componentPriceHistoryDao(),
            powderDao = database.powderDao(),
            primerDao = database.primerDao(),
            bulletDao = database.bulletDao(),
            brassDao = database.brassDao(),
        )
    }

    val factoryAmmoReferenceRepository: FactoryAmmoReferenceRepository by lazy {
        FactoryAmmoReferenceRepository()
    }
}
