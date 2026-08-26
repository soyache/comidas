package com.hectormeza.comidas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hectormeza.comidas.data.local.dao.CustomCurrencyDao
import com.hectormeza.comidas.data.local.dao.MealPriceDao
import com.hectormeza.comidas.data.local.dao.SettingDao
import com.hectormeza.comidas.data.local.dao.TransactionDao
import com.hectormeza.comidas.data.local.entity.AppSettingEntity
import com.hectormeza.comidas.data.local.entity.CustomCurrencyEntity
import com.hectormeza.comidas.data.local.entity.MealPriceEntity
import com.hectormeza.comidas.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        MealPriceEntity::class,
        AppSettingEntity::class,
        CustomCurrencyEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ComidasDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun mealPriceDao(): MealPriceDao
    abstract fun settingDao(): SettingDao
    abstract fun customCurrencyDao(): CustomCurrencyDao

    companion object {
        @Volatile
        private var INSTANCE: ComidasDatabase? = null

        fun getInstance(context: Context): ComidasDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ComidasDatabase::class.java,
                    "comidas_app_database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
