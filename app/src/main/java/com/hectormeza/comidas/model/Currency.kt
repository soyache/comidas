package com.hectormeza.comidas.model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class AppCurrency(
    val code: String,
    val symbol: String,
    val displayName: String,
    val flagEmoji: String = "🪙",
    val isCustom: Boolean = false
) {
    fun format(amount: Double, includeSymbol: Boolean = true): String {
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            groupingSeparator = ','
            decimalSeparator = '.'
        }
        val formatter = DecimalFormat("#,##0.00", symbols)
        val formattedNumber = formatter.format(amount)
        return if (includeSymbol) "$symbol $formattedNumber" else formattedNumber
    }

    companion object {
        val USD = AppCurrency("USD", "$", "Dólar estadounidense", "🇺🇸")
        val CORDOBA = AppCurrency("NIO", "C$", "Córdoba nicaragüense", "🇳🇮")
        val HNL = AppCurrency("HNL", "L", "Lempira hondureño", "🇭🇳")
        val EUR = AppCurrency("EUR", "€", "Euro", "🇪🇺")
        val MXN = AppCurrency("MXN", "MX$", "Peso mexicano", "🇲🇽")
        val COP = AppCurrency("COP", "COP$", "Peso colombiano", "🇨🇴")
        val CRC = AppCurrency("CRC", "₡", "Colón costarricense", "🇨🇷")
        val ARS = AppCurrency("ARS", "ARS$", "Peso argentino", "🇦🇷")

        val PREDEFINED_CURRENCIES = listOf(
            USD,
            CORDOBA,
            HNL,
            EUR,
            MXN,
            COP,
            CRC,
            ARS
        )
    }
}
