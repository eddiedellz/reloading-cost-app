package com.example.reloadcostcaluclator.data.repository

import androidx.room.withTransaction
import com.example.reloadcostcaluclator.data.local.dao.BrassDao
import com.example.reloadcostcaluclator.data.local.dao.BulletDao
import com.example.reloadcostcaluclator.data.local.dao.ComponentPriceHistoryDao
import com.example.reloadcostcaluclator.data.local.dao.PowderDao
import com.example.reloadcostcaluclator.data.local.dao.PrimerDao
import com.example.reloadcostcaluclator.data.local.dao.PurchaseOrderDao
import com.example.reloadcostcaluclator.data.local.db.ReloadingDatabase
import com.example.reloadcostcaluclator.data.local.entity.BrassEntity
import com.example.reloadcostcaluclator.data.local.entity.BulletEntity
import com.example.reloadcostcaluclator.data.local.entity.ComponentPriceHistoryEntity
import com.example.reloadcostcaluclator.data.local.entity.ComponentType
import com.example.reloadcostcaluclator.data.local.entity.ComponentUpdateMode
import com.example.reloadcostcaluclator.data.local.entity.ExtraChargeAllocationMethod
import com.example.reloadcostcaluclator.data.local.entity.ExtraChargeMode
import com.example.reloadcostcaluclator.data.local.entity.PowderEntity
import com.example.reloadcostcaluclator.data.local.entity.PrimerEntity
import com.example.reloadcostcaluclator.data.local.entity.PurchaseOrderEntity
import com.example.reloadcostcaluclator.data.local.entity.PurchaseOrderItemEntity
import kotlinx.coroutines.flow.Flow
import kotlin.math.max

data class CreateOrderItemInput(
    val componentType: ComponentType,
    val itemName: String,
    val unitPrice: Double,
    val packageQuantity: Double,
    val purchaseQuantity: Double,
    val lineSubtotal: Double,
    val allocatedExtraCharge: Double,
    val originalUnitCost: Double,
    val adjustedUnitCost: Double,
    val adjustedLineTotal: Double,
    val updateMode: ComponentUpdateMode,
)

data class PriceHistorySnapshot(
    val purchaseDateEpochMillis: Long,
    val landedCost: Double,
    val previousDifference: Double,
)

data class PriceHistorySummary(
    val weightedAverageCostPerUnit: Double,
    val latestLandedCost: Double,
    val history: List<PriceHistorySnapshot>,
)

class PurchaseOrderRepository(
    private val database: ReloadingDatabase,
    private val purchaseOrderDao: PurchaseOrderDao,
    private val priceHistoryDao: ComponentPriceHistoryDao,
    private val powderDao: PowderDao,
    private val primerDao: PrimerDao,
    private val bulletDao: BulletDao,
    private val brassDao: BrassDao,
) {
    suspend fun createOrder(
        purchaseDateEpochMillis: Long,
        extraChargeMode: ExtraChargeMode,
        allocationMethod: ExtraChargeAllocationMethod,
        orderTotal: Double,
        extraChargesTotal: Double,
        subtotal: Double,
        items: List<CreateOrderItemInput>,
    ) {
        if (items.isEmpty() || subtotal <= 0.0) return

        database.withTransaction {
            val orderId = purchaseOrderDao.insertOrder(
                PurchaseOrderEntity(
                    purchaseDateEpochMillis = purchaseDateEpochMillis,
                    extraChargeMode = extraChargeMode.name,
                    allocationMethod = allocationMethod.name,
                    orderTotal = orderTotal,
                    extraChargesTotal = extraChargesTotal,
                    subtotal = subtotal,
                    grandTotal = subtotal + extraChargesTotal,
                ),
            )

            val orderItemEntities = items.map {
                PurchaseOrderItemEntity(
                    orderId = orderId,
                    componentType = it.componentType.name,
                    itemName = it.itemName.trim(),
                    unitPrice = it.unitPrice,
                    packageQuantity = it.packageQuantity,
                    purchaseQuantity = it.purchaseQuantity,
                    lineSubtotal = it.lineSubtotal,
                    allocatedExtraCharge = it.allocatedExtraCharge,
                    originalUnitCost = it.originalUnitCost,
                    adjustedUnitCost = it.adjustedUnitCost,
                    adjustedLineTotal = it.adjustedLineTotal,
                    landedCost = it.adjustedLineTotal,
                )
            }
            val orderItemIds = purchaseOrderDao.insertOrderItems(orderItemEntities)

            val historyRecords = orderItemEntities.zip(orderItemIds).map { (item, itemId) ->
                ComponentPriceHistoryEntity(
                    componentType = item.componentType,
                    componentId = null,
                    componentName = item.itemName,
                    purchaseDateEpochMillis = purchaseDateEpochMillis,
                    orderId = orderId,
                    orderItemId = itemId,
                    quantity = item.packageQuantity * item.purchaseQuantity,
                    landedCost = item.landedCost,
                )
            }
            priceHistoryDao.insertAll(historyRecords)

            items.zip(orderItemEntities).forEach { (input, itemEntity) ->
                if (input.updateMode == ComponentUpdateMode.HISTORY_ONLY) return@forEach
                applyComponentUpdate(
                    input = input,
                    landedCost = itemEntity.landedCost,
                    quantity = max(0.000001, itemEntity.packageQuantity * itemEntity.purchaseQuantity),
                )
            }

            val mapped = historyRecords.map { history ->
                history.copy(componentId = findComponentId(ComponentType.valueOf(history.componentType), history.componentName))
            }
            priceHistoryDao.insertAll(mapped)
        }
    }

    fun getComponentPriceHistory(componentType: ComponentType, componentId: Long): Flow<List<ComponentPriceHistoryEntity>> {
        return priceHistoryDao.getByComponent(componentType.name, componentId)
    }

    suspend fun getPriceHistorySummary(componentType: ComponentType, componentName: String): PriceHistorySummary {
        val history = priceHistoryDao.getByTypeAndName(componentType.name, componentName)
        val chronological = history.sortedBy { it.purchaseDateEpochMillis }
        var previous: Double? = null
        val snapshots = chronological.map {
            val diff = if (previous == null) 0.0 else it.landedCost - previous!!
            previous = it.landedCost
            PriceHistorySnapshot(
                purchaseDateEpochMillis = it.purchaseDateEpochMillis,
                landedCost = it.landedCost,
                previousDifference = diff,
            )
        }.reversed()

        val totalCost = history.sumOf { it.landedCost }
        val totalQty = history.sumOf { it.quantity }
        val weighted = if (totalQty > 0.0) totalCost / totalQty else 0.0
        val latest = history.maxByOrNull { it.purchaseDateEpochMillis }?.landedCost ?: 0.0

        return PriceHistorySummary(
            weightedAverageCostPerUnit = weighted,
            latestLandedCost = latest,
            history = snapshots,
        )
    }

    private suspend fun applyComponentUpdate(input: CreateOrderItemInput, landedCost: Double, quantity: Double) {
        when (input.componentType) {
            ComponentType.POWDER -> {
                val existing = powderDao.getByName(input.itemName)
                val next = when {
                    existing == null -> PowderEntity(
                        name = input.itemName.trim(),
                        pricePaid = landedCost,
                        containerWeightLb = quantity,
                        pricingStrategy = input.updateMode.name,
                    )

                    input.updateMode == ComponentUpdateMode.LATEST_PRICE -> existing.copy(
                        pricePaid = landedCost,
                        containerWeightLb = quantity,
                        pricingStrategy = input.updateMode.name,
                    )

                    else -> {
                        val combinedCost = existing.pricePaid + landedCost
                        val combinedQty = existing.containerWeightLb + quantity
                        existing.copy(
                            pricePaid = combinedCost,
                            containerWeightLb = combinedQty,
                            pricingStrategy = input.updateMode.name,
                        )
                    }
                }
                if (existing == null) powderDao.insert(next) else powderDao.update(next)
            }

            ComponentType.PRIMER -> {
                val existing = primerDao.getByName(input.itemName)
                val quantityInt = quantity.toInt()
                val next = when {
                    existing == null -> PrimerEntity(
                        name = input.itemName.trim(),
                        pricePaid = landedCost,
                        quantity = quantityInt,
                        pricingStrategy = input.updateMode.name,
                    )

                    input.updateMode == ComponentUpdateMode.LATEST_PRICE -> existing.copy(
                        pricePaid = landedCost,
                        quantity = quantityInt,
                        pricingStrategy = input.updateMode.name,
                    )

                    else -> existing.copy(
                        pricePaid = existing.pricePaid + landedCost,
                        quantity = existing.quantity + quantityInt,
                        pricingStrategy = input.updateMode.name,
                    )
                }
                if (existing == null) primerDao.insert(next) else primerDao.update(next)
            }

            ComponentType.BULLET -> {
                val existing = bulletDao.getByName(input.itemName)
                val quantityInt = quantity.toInt()
                val next = when {
                    existing == null -> BulletEntity(
                        name = input.itemName.trim(),
                        grain = null,
                        bulletType = null,
                        pricePaid = landedCost,
                        quantity = quantityInt,
                        pricingStrategy = input.updateMode.name,
                    )

                    input.updateMode == ComponentUpdateMode.LATEST_PRICE -> existing.copy(
                        pricePaid = landedCost,
                        quantity = quantityInt,
                        pricingStrategy = input.updateMode.name,
                    )

                    else -> existing.copy(
                        pricePaid = existing.pricePaid + landedCost,
                        quantity = existing.quantity + quantityInt,
                        pricingStrategy = input.updateMode.name,
                    )
                }
                if (existing == null) bulletDao.insert(next) else bulletDao.update(next)
            }

            ComponentType.BRASS -> {
                val existing = brassDao.getByName(input.itemName)
                val quantityInt = quantity.toInt()
                val next = when {
                    existing == null -> BrassEntity(
                        name = input.itemName.trim(),
                        pricePaid = landedCost,
                        quantity = quantityInt,
                        reloadCount = 1,
                        pricingStrategy = input.updateMode.name,
                    )

                    input.updateMode == ComponentUpdateMode.LATEST_PRICE -> existing.copy(
                        pricePaid = landedCost,
                        quantity = quantityInt,
                        pricingStrategy = input.updateMode.name,
                    )

                    else -> existing.copy(
                        pricePaid = existing.pricePaid + landedCost,
                        quantity = existing.quantity + quantityInt,
                        pricingStrategy = input.updateMode.name,
                    )
                }
                if (existing == null) brassDao.insert(next) else brassDao.update(next)
            }
        }
    }

    private suspend fun findComponentId(type: ComponentType, name: String): Long? {
        return when (type) {
            ComponentType.POWDER -> powderDao.getByName(name)?.id
            ComponentType.PRIMER -> primerDao.getByName(name)?.id
            ComponentType.BULLET -> bulletDao.getByName(name)?.id
            ComponentType.BRASS -> brassDao.getByName(name)?.id
        }
    }
}
