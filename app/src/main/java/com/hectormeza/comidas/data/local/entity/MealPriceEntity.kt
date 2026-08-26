package com.hectormeza.comidas.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hectormeza.comidas.model.MealType

@Entity(tableName = "meal_prices")
data class MealPriceEntity(
    @PrimaryKey
    val mealType: String, // "BREAKFAST", "LUNCH", "DINNER"
    val price: Double
) {
    fun toDomainMealType(): MealType? {
        return try {
            MealType.valueOf(mealType)
        } catch (e: Exception) {
            null
        }
    }
}
