package com.hectormeza.comidas

import com.hectormeza.comidas.model.MealType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class MealTypeTest {

    @Test
    fun detectCurrent_usesExistingBreakfastLunchDinnerSlots() {
        assertEquals(MealType.BREAKFAST, MealType.detectCurrent(6))
        assertEquals(MealType.BREAKFAST, MealType.detectCurrent(11))
        assertEquals(MealType.LUNCH, MealType.detectCurrent(12))
        assertEquals(MealType.LUNCH, MealType.detectCurrent(17))
        assertEquals(MealType.DINNER, MealType.detectCurrent(18))
        assertEquals(MealType.DINNER, MealType.detectCurrent(23))
        assertEquals(MealType.DINNER, MealType.detectCurrent(0))
        assertEquals(MealType.DINNER, MealType.detectCurrent(5))
    }

    @Test
    fun detectAt_usesClockHourTheSameWayAsCenterButton() {
        val lunchTime = calendarAt(2026, Calendar.SEPTEMBER, 7, 15, 30).timeInMillis
        val breakfastTime = calendarAt(2026, Calendar.SEPTEMBER, 7, 8, 5).timeInMillis
        val dinnerTime = calendarAt(2026, Calendar.SEPTEMBER, 7, 21, 10).timeInMillis

        assertEquals(MealType.LUNCH, MealType.detectAt(lunchTime))
        assertEquals(MealType.BREAKFAST, MealType.detectAt(breakfastTime))
        assertEquals(MealType.DINNER, MealType.detectAt(dinnerTime))
    }

    @Test
    fun timestampOnDate_placesEachSlotOnTheSelectedDay() {
        val pastNoon = calendarAt(2026, Calendar.AUGUST, 20, 12, 0).timeInMillis

        val breakfast = Calendar.getInstance().apply {
            timeInMillis = MealType.BREAKFAST.timestampOnDate(pastNoon)
        }
        val lunch = Calendar.getInstance().apply {
            timeInMillis = MealType.LUNCH.timestampOnDate(pastNoon)
        }
        val dinner = Calendar.getInstance().apply {
            timeInMillis = MealType.DINNER.timestampOnDate(pastNoon)
        }

        assertEquals(2026, breakfast.get(Calendar.YEAR))
        assertEquals(Calendar.AUGUST, breakfast.get(Calendar.MONTH))
        assertEquals(20, breakfast.get(Calendar.DAY_OF_MONTH))
        assertEquals(MealType.BREAKFAST.startHour, breakfast.get(Calendar.HOUR_OF_DAY))

        assertEquals(20, lunch.get(Calendar.DAY_OF_MONTH))
        assertEquals(MealType.LUNCH.startHour, lunch.get(Calendar.HOUR_OF_DAY))

        assertEquals(20, dinner.get(Calendar.DAY_OF_MONTH))
        assertEquals(MealType.DINNER.startHour, dinner.get(Calendar.HOUR_OF_DAY))

        assertNotEquals(breakfast.timeInMillis, lunch.timeInMillis)
        assertNotEquals(lunch.timeInMillis, dinner.timeInMillis)
    }

    @Test
    fun pastDaySlots_areDistinctRecordsNotACombinedTimestamp() {
        val dateAnchor = calendarAt(2026, Calendar.JULY, 3, 12, 0).timeInMillis
        val timestamps = MealType.entries.map { it.timestampOnDate(dateAnchor) }.toSet()

        assertEquals(MealType.entries.size, timestamps.size)
    }

    private fun calendarAt(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int
    ): Calendar {
        return Calendar.getInstance(TimeZone.getDefault()).apply {
            clear()
            set(year, month, day, hour, minute, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
}
