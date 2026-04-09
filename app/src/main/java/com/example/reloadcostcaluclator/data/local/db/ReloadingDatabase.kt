package com.example.reloadcostcaluclator.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.reloadcostcaluclator.data.local.dao.BrassDao
import com.example.reloadcostcaluclator.data.local.dao.BulletDao
import com.example.reloadcostcaluclator.data.local.dao.ComponentPriceHistoryDao
import com.example.reloadcostcaluclator.data.local.dao.FactoryComparisonDao
import com.example.reloadcostcaluclator.data.local.dao.LoadRecipeDao
import com.example.reloadcostcaluclator.data.local.dao.PowderDao
import com.example.reloadcostcaluclator.data.local.dao.PrimerDao
import com.example.reloadcostcaluclator.data.local.dao.PurchaseOrderDao
import com.example.reloadcostcaluclator.data.local.entity.BrassEntity
import com.example.reloadcostcaluclator.data.local.entity.BulletEntity
import com.example.reloadcostcaluclator.data.local.entity.ComponentPriceHistoryEntity
import com.example.reloadcostcaluclator.data.local.entity.ComponentUpdateMode
import com.example.reloadcostcaluclator.data.local.entity.ExtraChargeAllocationMethod
import com.example.reloadcostcaluclator.data.local.entity.ExtraChargeMode
import com.example.reloadcostcaluclator.data.local.entity.FactoryComparisonEntity
import com.example.reloadcostcaluclator.data.local.entity.LoadRecipeEntity
import com.example.reloadcostcaluclator.data.local.entity.PowderEntity
import com.example.reloadcostcaluclator.data.local.entity.PrimerEntity
import com.example.reloadcostcaluclator.data.local.entity.PurchaseOrderEntity
import com.example.reloadcostcaluclator.data.local.entity.PurchaseOrderItemEntity

@Database(
    entities = [
        PowderEntity::class,
        PrimerEntity::class,
        BulletEntity::class,
        BrassEntity::class,
        LoadRecipeEntity::class,
        PurchaseOrderEntity::class,
        PurchaseOrderItemEntity::class,
        ComponentPriceHistoryEntity::class,
        FactoryComparisonEntity::class,
    ],
    version = 6,
    exportSchema = false,
)
abstract class ReloadingDatabase : RoomDatabase() {
    abstract fun powderDao(): PowderDao
    abstract fun primerDao(): PrimerDao
    abstract fun bulletDao(): BulletDao
    abstract fun brassDao(): BrassDao
    abstract fun loadRecipeDao(): LoadRecipeDao
    abstract fun purchaseOrderDao(): PurchaseOrderDao
    abstract fun componentPriceHistoryDao(): ComponentPriceHistoryDao
    abstract fun factoryComparisonDao(): FactoryComparisonDao
}

object ReloadingDatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE bullets ADD COLUMN grain INTEGER")
            database.execSQL("ALTER TABLE bullets ADD COLUMN bulletType TEXT")
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            val defaultMode = ComponentUpdateMode.LATEST_PRICE.name
            database.execSQL("ALTER TABLE powders ADD COLUMN pricingStrategy TEXT NOT NULL DEFAULT '$defaultMode'")
            database.execSQL("ALTER TABLE primers ADD COLUMN pricingStrategy TEXT NOT NULL DEFAULT '$defaultMode'")
            database.execSQL("ALTER TABLE bullets ADD COLUMN pricingStrategy TEXT NOT NULL DEFAULT '$defaultMode'")
            database.execSQL("ALTER TABLE brass ADD COLUMN pricingStrategy TEXT NOT NULL DEFAULT '$defaultMode'")

            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS purchase_orders (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    purchaseDateEpochMillis INTEGER NOT NULL,
                    extraChargesTotal REAL NOT NULL,
                    subtotal REAL NOT NULL,
                    grandTotal REAL NOT NULL
                )
                """.trimIndent(),
            )

            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS purchase_order_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    orderId INTEGER NOT NULL,
                    componentType TEXT NOT NULL,
                    itemName TEXT NOT NULL,
                    quantityOrPackageSize REAL NOT NULL,
                    basePrice REAL NOT NULL,
                    allocatedExtraCharge REAL NOT NULL,
                    landedCost REAL NOT NULL,
                    FOREIGN KEY(orderId) REFERENCES purchase_orders(id) ON DELETE CASCADE
                )
                """.trimIndent(),
            )
            database.execSQL("CREATE INDEX IF NOT EXISTS index_purchase_order_items_orderId ON purchase_order_items(orderId)")

            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS component_price_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    componentType TEXT NOT NULL,
                    componentId INTEGER,
                    componentName TEXT NOT NULL,
                    purchaseDateEpochMillis INTEGER NOT NULL,
                    orderId INTEGER NOT NULL,
                    orderItemId INTEGER NOT NULL,
                    quantity REAL NOT NULL,
                    landedCost REAL NOT NULL
                )
                """.trimIndent(),
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_component_price_history_componentType_componentId ON component_price_history(componentType, componentId)",
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_component_price_history_componentType_componentName ON component_price_history(componentType, componentName)",
            )
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            val defaultChargeMode = ExtraChargeMode.MANUAL_EXTRA_CHARGES.name
            val defaultAllocation = ExtraChargeAllocationMethod.PROPORTIONAL_BY_LINE_SUBTOTAL.name
            database.execSQL("ALTER TABLE purchase_orders ADD COLUMN extraChargeMode TEXT NOT NULL DEFAULT '$defaultChargeMode'")
            database.execSQL("ALTER TABLE purchase_orders ADD COLUMN allocationMethod TEXT NOT NULL DEFAULT '$defaultAllocation'")
            database.execSQL("ALTER TABLE purchase_orders ADD COLUMN orderTotal REAL NOT NULL DEFAULT 0")
            database.execSQL("UPDATE purchase_orders SET orderTotal = grandTotal")

            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS purchase_order_items_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    orderId INTEGER NOT NULL,
                    componentType TEXT NOT NULL,
                    itemName TEXT NOT NULL,
                    unitPrice REAL NOT NULL,
                    packageQuantity REAL NOT NULL,
                    purchaseQuantity REAL NOT NULL,
                    lineSubtotal REAL NOT NULL,
                    allocatedExtraCharge REAL NOT NULL,
                    originalUnitCost REAL NOT NULL,
                    adjustedUnitCost REAL NOT NULL,
                    adjustedLineTotal REAL NOT NULL,
                    landedCost REAL NOT NULL,
                    FOREIGN KEY(orderId) REFERENCES purchase_orders(id) ON DELETE CASCADE
                )
                """.trimIndent(),
            )

            database.execSQL(
                """
                INSERT INTO purchase_order_items_new (
                    id, orderId, componentType, itemName, unitPrice, packageQuantity, purchaseQuantity,
                    lineSubtotal, allocatedExtraCharge, originalUnitCost, adjustedUnitCost, adjustedLineTotal, landedCost
                )
                SELECT
                    id,
                    orderId,
                    componentType,
                    itemName,
                    basePrice,
                    quantityOrPackageSize,
                    1.0,
                    basePrice,
                    allocatedExtraCharge,
                    basePrice,
                    landedCost,
                    landedCost,
                    landedCost
                FROM purchase_order_items
                """.trimIndent(),
            )

            database.execSQL("DROP TABLE purchase_order_items")
            database.execSQL("ALTER TABLE purchase_order_items_new RENAME TO purchase_order_items")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_purchase_order_items_orderId ON purchase_order_items(orderId)")
        }
    }

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS factory_comparisons (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    brand TEXT NOT NULL,
                    productName TEXT NOT NULL,
                    caliber TEXT NOT NULL,
                    grain INTEGER NOT NULL,
                    bulletType TEXT,
                    boxQuantity INTEGER NOT NULL,
                    totalPrice REAL NOT NULL,
                    costPerRound REAL NOT NULL,
                    notes TEXT NOT NULL,
                    createdAtEpochMillis INTEGER NOT NULL,
                    updatedAtEpochMillis INTEGER NOT NULL
                )
                """.trimIndent(),
            )
        }
    }


    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS purchase_orders_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    purchaseDateEpochMillis INTEGER NOT NULL,
                    extraChargeMode TEXT NOT NULL,
                    allocationMethod TEXT NOT NULL,
                    totalCents INTEGER NOT NULL,
                    extraChargesCents INTEGER NOT NULL,
                    subtotalCents INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            database.execSQL(
                """
                INSERT INTO purchase_orders_new (
                    id, purchaseDateEpochMillis, extraChargeMode, allocationMethod, totalCents, extraChargesCents, subtotalCents
                )
                SELECT
                    id,
                    purchaseDateEpochMillis,
                    extraChargeMode,
                    allocationMethod,
                    CAST(ROUND(orderTotal * 100.0) AS INTEGER),
                    CAST(ROUND(extraChargesTotal * 100.0) AS INTEGER),
                    CAST(ROUND(subtotal * 100.0) AS INTEGER)
                FROM purchase_orders
                """.trimIndent(),
            )
            database.execSQL("DROP TABLE purchase_orders")
            database.execSQL("ALTER TABLE purchase_orders_new RENAME TO purchase_orders")

            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS purchase_order_items_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    orderId INTEGER NOT NULL,
                    componentType TEXT NOT NULL,
                    itemName TEXT NOT NULL,
                    unitPriceCents INTEGER NOT NULL,
                    packageQuantity REAL NOT NULL,
                    purchaseQuantity REAL NOT NULL,
                    lineSubtotalCents INTEGER NOT NULL,
                    allocatedExtraChargeCents INTEGER NOT NULL,
                    originalUnitCostCents INTEGER NOT NULL,
                    adjustedUnitCostCents INTEGER NOT NULL,
                    adjustedLineTotalCents INTEGER NOT NULL,
                    landedCostCents INTEGER NOT NULL,
                    FOREIGN KEY(orderId) REFERENCES purchase_orders(id) ON DELETE CASCADE
                )
                """.trimIndent(),
            )
            database.execSQL(
                """
                INSERT INTO purchase_order_items_new (
                    id, orderId, componentType, itemName, unitPriceCents, packageQuantity, purchaseQuantity,
                    lineSubtotalCents, allocatedExtraChargeCents, originalUnitCostCents, adjustedUnitCostCents,
                    adjustedLineTotalCents, landedCostCents
                )
                SELECT
                    id,
                    orderId,
                    componentType,
                    itemName,
                    CAST(ROUND(unitPrice * 100.0) AS INTEGER),
                    packageQuantity,
                    purchaseQuantity,
                    CAST(ROUND(lineSubtotal * 100.0) AS INTEGER),
                    CAST(ROUND(allocatedExtraCharge * 100.0) AS INTEGER),
                    CAST(ROUND(originalUnitCost * 100.0) AS INTEGER),
                    CAST(ROUND(adjustedUnitCost * 100.0) AS INTEGER),
                    CAST(ROUND(adjustedLineTotal * 100.0) AS INTEGER),
                    CAST(ROUND(landedCost * 100.0) AS INTEGER)
                FROM purchase_order_items
                """.trimIndent(),
            )
            database.execSQL("DROP TABLE purchase_order_items")
            database.execSQL("ALTER TABLE purchase_order_items_new RENAME TO purchase_order_items")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_purchase_order_items_orderId ON purchase_order_items(orderId)")
        }
    }
}
