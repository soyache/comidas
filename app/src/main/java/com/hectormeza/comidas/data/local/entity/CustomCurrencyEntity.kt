package com.hectormeza.comidas.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hectormeza.comidas.model.AppCurrency

@Entity(tableName = "custom_currencies")
data class CustomCurrencyEntity(
    @PrimaryKey
    val code: String,
    val symbol: String,
    val displayName: String,
    val flagEmoji: String = "🪙"
) {
    fun toDomain(): AppCurrency {
        return AppCurrency(
            code = code,
            symbol = symbol,
            displayName = displayName,
            flagEmoji = flagEmoji,
            isCustom = true
        )
    }

    companion object {
        fun fromDomain(currency: AppCurrency): CustomCurrencyEntity {
            return CustomCurrencyEntity(
                code = currency.code,
                symbol = currency.symbol,
                displayName = currency.displayName,
                flagEmoji = currency.flagEmoji
            )
        }
    }
}
