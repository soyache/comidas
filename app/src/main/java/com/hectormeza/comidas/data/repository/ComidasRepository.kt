package com.hectormeza.comidas.data.repository

import android.content.Context
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

    private val _activeCurrency = MutableStateFlow(AppCurrency.USD)
    val activeCurrency: StateFlow<AppCurrency> = _activeCurrency.asStateFlow()

    private val _hasCompletedOnboarding = MutableStateFlow(false)
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    suspend fun initialize() {
        // Load custom currencies
        val customCurrencies = dbHelper.getAllCustomCurrencies()
        val allCurrencies = AppCurrency.PREDEFINED_CURRENCIES + customCurrencies
        _availableCurrencies.value = allCurrencies

        // Load active currency setting (Defaults to USD)
        val activeCode = dbHelper.getSetting(KEY_ACTIVE_CURRENCY_CODE)
        if (activeCode != null) {
            _activeCurrency.value = allCurrencies.find { it.code == activeCode } ?: AppCurrency.USD
        } else {
            dbHelper.setSetting(KEY_ACTIVE_CURRENCY_CODE, AppCurrency.USD.code)
            _activeCurrency.value = AppCurrency.USD
        }

        // Load or seed meal prices
        val savedPrices = dbHelper.getAllPrices()
        if (savedPrices.isEmpty()) {
            MealType.entries.forEach { meal ->
                dbHelper.setMealPrice(meal, meal.defaultPrice)
            }
            _mealPrices.value = dbHelper.getAllPrices()
        } else {
            _mealPrices.value = savedPrices
        }

        // Load onboarding completion status
        val onboardingStatus = dbHelper.getSetting(KEY_ONBOARDING_COMPLETED)
        _hasCompletedOnboarding.value = onboardingStatus == "true"

        // Load transactions (Empty by default, zero demo data)
        _transactions.value = dbHelper.getAllTransactions()
    }

    suspend fun completeOnboarding() {
        dbHelper.setSetting(KEY_ONBOARDING_COMPLETED, "true")
        _hasCompletedOnboarding.value = true
    }

    suspend fun showOnboardingAgain() {
        _hasCompletedOnboarding.value = false
    }

    suspend fun addTransaction(transaction: Transaction) {
        dbHelper.insertTransaction(transaction)
        _transactions.value = dbHelper.getAllTransactions()
    }

    suspend fun addMealTransaction(
        mealType: MealType,
        quantity: Int,
        timestamp: Long = System.currentTimeMillis(),
        note: String? = null
    ): Transaction {
        val unitPrice = _mealPrices.value[mealType] ?: mealType.defaultPrice
        val transaction = Transaction(
            id = UUID.randomUUID().toString(),
            type = TransactionType.MEAL,
            amount = unitPrice * quantity,
            mealType = mealType,
            quantity = quantity,
            note = note?.ifBlank { null },
            timestamp = timestamp
        )
        addTransaction(transaction)
        return transaction
    }

    suspend fun addCurrentMealFromClock(nowMillis: Long = System.currentTimeMillis()): Transaction {
        return addMealTransaction(
            mealType = MealType.detectAt(nowMillis),
            quantity = 1,
            timestamp = nowMillis
        )
    }

    suspend fun addPastMeals(
        mealTypes: Collection<MealType>,
        quantity: Int,
        dateMillis: Long,
        note: String?
    ): List<Transaction> {
        return MealType.entries.filter { it in mealTypes }.map { mealType ->
            addMealTransaction(
                mealType = mealType,
                quantity = quantity,
                timestamp = mealType.timestampOnDate(dateMillis),
                note = note
            )
        }
    }

    suspend fun updateTransaction(transaction: Transaction) {
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

    suspend fun resetData() {
        dbHelper.clearTransactions()
        dbHelper.clearCustomCurrencies()

        // Reset default USD prices
        MealType.entries.forEach { meal ->
            dbHelper.setMealPrice(meal, meal.defaultPrice)
        }
        _mealPrices.value = dbHelper.getAllPrices()

        // Reset currency to USD
        setActiveCurrency(AppCurrency.USD)
        _availableCurrencies.value = AppCurrency.PREDEFINED_CURRENCIES

        // Empty transactions
        _transactions.value = emptyList()
    }

    companion object {
        private const val KEY_ACTIVE_CURRENCY_CODE = "active_currency_code"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"

        @Volatile
        private var instance: ComidasRepository? = null

        fun getInstance(context: Context): ComidasRepository {
            return instance ?: synchronized(this) {
                instance ?: ComidasRepository(
                    ComidasDatabaseHelper(context.applicationContext)
                ).also { instance = it }
            }
        }
    }
}
