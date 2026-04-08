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
