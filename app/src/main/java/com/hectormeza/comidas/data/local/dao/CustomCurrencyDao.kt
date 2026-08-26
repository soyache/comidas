package com.hectormeza.comidas.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hectormeza.comidas.data.local.entity.CustomCurrencyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomCurrencyDao {
    @Query("SELECT * FROM custom_currencies")
    fun getAllCustomCurrencies(): Flow<List<CustomCurrencyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomCurrency(currency: CustomCurrencyEntity)

    @Query("DELETE FROM custom_currencies")
    suspend fun clearAll()
}
