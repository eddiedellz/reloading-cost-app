package com.example.reloadcostcaluclator.data.reference

import com.example.reloadcostcaluclator.model.FactoryAmmoReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FactoryAmmoReferenceRepository {
    private val references = listOf(
        FactoryAmmoReference(
            caliber = "9mm Luger",
            grain = 115,
            bulletType = "FMJ",
            name = "Blazer Brass 115gr FMJ",
            pricePerRound = 0.30,
            pricePer50 = 15.00,
            notes = "Common range baseline.",
        ),
        FactoryAmmoReference(
            caliber = "9mm Luger",
            grain = 124,
            bulletType = "JHP",
            name = "Federal HST 124gr JHP",
            pricePerRound = 1.08,
            pricePer50 = 54.00,
            notes = "Premium defensive load.",
        ),
        FactoryAmmoReference(
            caliber = ".223 Rem",
            grain = 55,
            bulletType = "FMJ",
            name = "PMC Bronze 55gr FMJ",
            pricePerRound = 0.58,
            pricePer50 = 29.00,
            notes = "Bulk training reference.",
        ),
        FactoryAmmoReference(
            caliber = ".308 Win",
            grain = 168,
            bulletType = "HPBT",
            name = "Federal Gold Medal Match 168gr HPBT",
            pricePerRound = 1.90,
            pricePer50 = 95.00,
            notes = "Match-grade benchmark.",
        ),
    )

    fun getAll(): Flow<List<FactoryAmmoReference>> = flowOf(references)
}
