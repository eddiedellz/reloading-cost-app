package com.example.reloadcostcaluclator.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.reloadcostcaluclator.data.local.entity.LoadRecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoadRecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(loadRecipe: LoadRecipeEntity): Long

    @Update
    suspend fun update(loadRecipe: LoadRecipeEntity)

    @Delete
    suspend fun delete(loadRecipe: LoadRecipeEntity)

    @Query("SELECT * FROM load_recipes ORDER BY name ASC")
    fun getAll(): Flow<List<LoadRecipeEntity>>

    @Query("SELECT * FROM load_recipes WHERE id = :id")
    fun getById(id: Long): Flow<LoadRecipeEntity?>
}
