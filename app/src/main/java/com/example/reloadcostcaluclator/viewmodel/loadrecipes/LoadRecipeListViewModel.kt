package com.example.reloadcostcaluclator.viewmodel.loadrecipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reloadcostcaluclator.data.local.entity.LoadRecipeEntity
import com.example.reloadcostcaluclator.data.repository.LoadRecipeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoadRecipeListViewModel(
    private val repository: LoadRecipeRepository,
) : ViewModel() {
    val loadRecipes: StateFlow<List<LoadRecipeEntity>> = repository.getAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    fun delete(loadRecipe: LoadRecipeEntity) {
        viewModelScope.launch {
            repository.delete(loadRecipe)
        }
    }

    companion object {
        fun provideFactory(repository: LoadRecipeRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LoadRecipeListViewModel(repository) as T
                }
            }
    }
}
