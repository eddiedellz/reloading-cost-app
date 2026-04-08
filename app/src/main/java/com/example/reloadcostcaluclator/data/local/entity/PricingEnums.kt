package com.example.reloadcostcaluclator.data.local.entity

enum class ComponentType {
    POWDER,
    PRIMER,
    BULLET,
    BRASS,
}

enum class ComponentUpdateMode {
    LATEST_PRICE,
    WEIGHTED_AVERAGE,
    HISTORY_ONLY,
}

enum class ExtraChargeMode {
    MANUAL_EXTRA_CHARGES,
    USE_ORDER_TOTAL,
}

enum class ExtraChargeAllocationMethod {
    PROPORTIONAL_BY_LINE_SUBTOTAL,
    EVEN_BY_QUANTITY,
}
