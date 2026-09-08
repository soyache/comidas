package com.hectormeza.comidas

import com.hectormeza.comidas.model.MealType
import com.hectormeza.comidas.model.Transaction
import com.hectormeza.comidas.model.TransactionType
import com.hectormeza.comidas.model.formatMovimientoDate
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class TransactionDateFormatTest {

    @Test
    fun formattedDate_includesSpanishWeekdayAndSameCalendarDate() {
        val tuesday = calendarAt(2026, Calendar.SEPTEMBER, 8, 13, 15).timeInMillis
        val transaction = Transaction(
            id = "1",
            type = TransactionType.MEAL,
            amount = 130.0,
            timestamp = tuesday,
            mealType = MealType.LUNCH
        )

        val formatted = transaction.formattedDate.lowercase(Locale.forLanguageTag("es"))

        assertTrue(formatted.contains("martes"))
        assertTrue(formatted.contains("8"))
        assertTrue(formatted.contains("sep"))
        assertTrue(formatted.contains("2026"))
        assertFalse(formatted.matches(Regex("^\\s*martes\\s*$")))
    }

    @Test
    fun formatMovimientoDate_keepsWeekdayAndNumericDateTogether() {
        val monday = calendarAt(2026, Calendar.JANUARY, 5, 9, 0).timeInMillis
        val formatted = formatMovimientoDate(monday).lowercase(Locale.forLanguageTag("es"))

        assertTrue(formatted.contains("lunes"))
        assertTrue(formatted.contains("5"))
        assertTrue(formatted.contains("2026"))
        assertTrue(formatted.contains("lunes") && formatted.any { it.isDigit() })
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
