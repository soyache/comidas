package com.hectormeza.comidas.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hectormeza.comidas.data.local.entity.MealPriceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPriceDao {
    @Query("SELECT * FROM meal_prices")
    fun getAllPrices(): Flow<List<MealPriceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(price: MealPriceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(prices: List<MealPriceEntity>)

    @Query("DELETE FROM meal_prices")
    suspend fun clearAll()
}
