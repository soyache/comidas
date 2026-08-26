package com.hectormeza.comidas.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hectormeza.comidas.model.MealType
import com.hectormeza.comidas.model.Transaction
import com.hectormeza.comidas.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val type: String, // "MEAL" or "PAYMENT"
    val amount: Double,
    val timestamp: Long,
    val mealType: String? = null, // "BREAKFAST", "LUNCH", "DINNER"
    val quantity: Int = 1,
    val note: String? = null
) {
    fun toDomain(): Transaction {
        val domainType = if (type == "PAYMENT") TransactionType.PAYMENT else TransactionType.MEAL
        val domainMealType = mealType?.let {
            try {
                MealType.valueOf(it)
            } catch (e: Exception) {
                null
            }
        }

        return Transaction(
            id = id,
            type = domainType,
            amount = amount,
            timestamp = timestamp,
            mealType = domainMealType,
            quantity = quantity,
            note = note
        )
    }

    companion object {
        fun fromDomain(transaction: Transaction): TransactionEntity {
            return TransactionEntity(
                id = transaction.id,
                type = transaction.type.name,
                amount = transaction.amount,
                timestamp = transaction.timestamp,
                mealType = transaction.mealType?.name,
                quantity = transaction.quantity,
                note = transaction.note
            )
        }
    }
}
