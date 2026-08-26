package com.hectormeza.comidas.data.repository

import com.hectormeza.comidas.data.local.ComidasDatabaseHelper
import com.hectormeza.comidas.model.AppCurrency
import com.hectormeza.comidas.model.MealType
import com.hectormeza.comidas.model.Transaction
import com.hectormeza.comidas.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class ComidasRepository(
    private val dbHelper: ComidasDatabaseHelper
) {
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _mealPrices = MutableStateFlow<Map<MealType, Double>>(
        mapOf(
            MealType.BREAKFAST to MealType.BREAKFAST.defaultPrice,
            MealType.LUNCH to MealType.LUNCH.defaultPrice,
            MealType.DINNER to MealType.DINNER.defaultPrice
        )
    )
    val mealPrices: StateFlow<Map<MealType, Double>> = _mealPrices.asStateFlow()

    private val _availableCurrencies = MutableStateFlow<List<AppCurrency>>(AppCurrency.PREDEFINED_CURRENCIES)
    val availableCurrencies: StateFlow<List<AppCurrency>> = _availableCurrencies.asStateFlow()

    private val _activeCurrency = MutableStateFlow(AppCurrency.CORDOBA)
    val activeCurrency: StateFlow<AppCurrency> = _activeCurrency.asStateFlow()

    suspend fun initialize() {
        // Load custom currencies
        val customCurrencies = dbHelper.getAllCustomCurrencies()
        val allCurrencies = AppCurrency.PREDEFINED_CURRENCIES + customCurrencies
        _availableCurrencies.value = allCurrencies

        // Load active currency setting
        val activeCode = dbHelper.getSetting(KEY_ACTIVE_CURRENCY_CODE)
        if (activeCode != null) {
            _activeCurrency.value = allCurrencies.find { it.code == activeCode } ?: AppCurrency.CORDOBA
        } else {
            dbHelper.setSetting(KEY_ACTIVE_CURRENCY_CODE, AppCurrency.CORDOBA.code)
            _activeCurrency.value = AppCurrency.CORDOBA
        }

        // Load or seed meal prices
        val savedPrices = dbHelper.getAllPrices()
        _mealPrices.value = savedPrices

        // Load transactions
        val savedTx = dbHelper.getAllTransactions()
        if (savedTx.isEmpty()) {
            // Seed initial demo transactions on very first launch
            resetToDemoData()
        } else {
            _transactions.value = savedTx
        }
    }

    suspend fun addTransaction(transaction: Transaction) {
        dbHelper.insertTransaction(transaction)
        _transactions.value = dbHelper.getAllTransactions()
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        dbHelper.deleteTransaction(transaction.id)
        _transactions.value = dbHelper.getAllTransactions()
    }

    suspend fun updateMealPrice(mealType: MealType, price: Double) {
        dbHelper.setMealPrice(mealType, price)
        _mealPrices.value = dbHelper.getAllPrices()
    }

    suspend fun setActiveCurrency(currency: AppCurrency) {
        dbHelper.setSetting(KEY_ACTIVE_CURRENCY_CODE, currency.code)
        _activeCurrency.value = currency
    }

    suspend fun addCustomCurrency(currency: AppCurrency) {
        dbHelper.insertCustomCurrency(currency)
        val customCurrencies = dbHelper.getAllCustomCurrencies()
        _availableCurrencies.value = AppCurrency.PREDEFINED_CURRENCIES + customCurrencies
        setActiveCurrency(currency)
    }

    suspend fun resetToDemoData() {
        dbHelper.clearTransactions()
        dbHelper.clearCustomCurrencies()

        // Reset default prices
        MealType.entries.forEach { meal ->
            dbHelper.setMealPrice(meal, meal.defaultPrice)
        }
        _mealPrices.value = dbHelper.getAllPrices()

        // Reset currency to NIO
        setActiveCurrency(AppCurrency.CORDOBA)
        _availableCurrencies.value = AppCurrency.PREDEFINED_CURRENCIES

        // Seed demo transactions
        val now = System.currentTimeMillis()
        val demoList = listOf(
            Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.MEAL,
                amount = 130.0,
                mealType = MealType.LUNCH,
                quantity = 1,
                timestamp = now - 1000 * 60 * 60 * 3
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.MEAL,
                amount = 80.0,
                mealType = MealType.BREAKFAST,
                quantity = 1,
                timestamp = now - 1000 * 60 * 60 * 7
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.PAYMENT,
                amount = 100.0,
                note = "Abono inicial",
                timestamp = now - 1000 * 60 * 60 * 24
            )
        )

        for (tx in demoList) {
            dbHelper.insertTransaction(tx)
        }
        _transactions.value = dbHelper.getAllTransactions()
    }

    companion object {
        private const val KEY_ACTIVE_CURRENCY_CODE = "active_currency_code"
    }
}
