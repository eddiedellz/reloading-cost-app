package com.example.reloadcostcaluclator.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reloadcostcaluclator.data.repository.FactoryComparisonRepository

class FactoryComparisonListViewModel(
    repository: FactoryComparisonRepository,
) : ViewModel() {
    val comparisons = repository.getAll()

    companion object {
        fun provideFactory(repository: FactoryComparisonRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FactoryComparisonListViewModel(repository) as T
                }
            }
    }
}
