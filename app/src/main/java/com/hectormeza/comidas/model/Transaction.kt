package com.hectormeza.comidas.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TransactionType {
    MEAL,
    PAYMENT
}

data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val mealType: MealType? = null,
    val quantity: Int = 1,
    val note: String? = null
) {
    val formattedTime: String
        get() {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

    val displayTitle: String
        get() = when (type) {
            TransactionType.MEAL -> {
                val base = mealType?.displayName ?: "Comida"
                if (quantity > 1) "$quantity× $base" else base
            }
            TransactionType.PAYMENT -> if (!note.isNullOrBlank()) "Abono - $note" else "Abono a deuda"
        }
}
