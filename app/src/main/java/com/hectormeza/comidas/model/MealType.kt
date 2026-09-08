package com.hectormeza.comidas.model

import java.util.Calendar

enum class MealType(
    val id: String,
    val displayName: String,
    val defaultPrice: Double,
    val timeRangeDescription: String,
    val startHour: Int,
    val endHour: Int
) {
    BREAKFAST(
        id = "breakfast",
        displayName = "Desayuno",
        defaultPrice = 3.0,
        timeRangeDescription = "06:00 - 11:59",
        startHour = 6,
        endHour = 11
    ),
    LUNCH(
        id = "lunch",
        displayName = "Almuerzo",
        defaultPrice = 5.0,
        timeRangeDescription = "12:00 - 17:59",
        startHour = 12,
        endHour = 17
    ),
    DINNER(
        id = "dinner",
        displayName = "Cena",
        defaultPrice = 4.0,
        timeRangeDescription = "18:00 - 05:59",
        startHour = 18,
        endHour = 5
    );

    fun timestampOnDate(dateMillis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = dateMillis
            set(Calendar.HOUR_OF_DAY, startHour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    companion object {
        fun detectCurrent(hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): MealType {
            return when (hour) {
                in 6..11 -> BREAKFAST
                in 12..17 -> LUNCH
                else -> DINNER
            }
        }

        fun detectAt(timestampMillis: Long): MealType {
            val hour = Calendar.getInstance().apply {
                timeInMillis = timestampMillis
            }.get(Calendar.HOUR_OF_DAY)
            return detectCurrent(hour)
        }
    }
}
