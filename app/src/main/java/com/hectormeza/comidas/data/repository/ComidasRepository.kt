package com.hectormeza.comidas.data.repository

import com.hectormeza.comidas.data.local.dao.CustomCurrencyDao
import com.hectormeza.comidas.data.local.dao.MealPriceDao
import com.hectormeza.comidas.data.local.dao.SettingDao
import com.hectormeza.comidas.data.local.dao.TransactionDao
import com.hectormeza.comidas.data.local.entity.AppSettingEntity
import com.hectormeza.comidas.data.local.entity.CustomCurrencyEntity
import com.hectormeza.comidas.data.local.entity.MealPriceEntity
import com.hectormeza.comidas.data.local.entity.TransactionEntity
import com.hectormeza.comidas.model.AppCurrency
import com.hectormeza.comidas.model.MealType
import com.hectormeza.comidas.model.Transaction
import com.hectormeza.comidas.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.UUID

class ComidasRepository(
    private val transactionDao: TransactionDao,
    private val mealPriceDao: MealPriceDao,
    private val settingDao: SettingDao,
    private val customCurrencyDao: CustomCurrencyDao
) {
    // 1. Transactions Stream
    val transactions: Flow<List<Transaction>> = transactionDao.getAllTransactions().map { list ->
        list.map { it.toDomain() }
    }

    // 2. Meal Prices Stream
    val mealPrices: Flow<Map<MealType, Double>> = mealPriceDao.getAllPrices().map { list ->
        val map = mutableMapOf(
            MealType.BREAKFAST to MealType.BREAKFAST.defaultPrice,
            MealType.LUNCH to MealType.LUNCH.defaultPrice,
            MealType.DINNER to MealType.DINNER.defaultPrice
        )
        list.forEach { entity ->
            val mealType = entity.toDomainMealType()
            if (mealType != null) {
                map[mealType] = entity.price
            }
        }
        map
    }

    // 3. Available Currencies (Predefined + Custom from DB)
    val availableCurrencies: Flow<List<AppCurrency>> = customCurrencyDao.getAllCustomCurrencies().map { customList ->
        val predefined = AppCurrency.PREDEFINED_CURRENCIES
        val custom = customList.map { it.toDomain() }
        predefined + custom
    }

    // 4. Active Currency Stream
    val activeCurrency: Flow<AppCurrency> = combine(
        settingDao.getSetting(KEY_ACTIVE_CURRENCY_CODE),
        availableCurrencies
    ) { activeCode, allCurrencies ->
        if (activeCode == null) {
            AppCurrency.CORDOBA
        } else {
            allCurrencies.find { it.code == activeCode } ?: AppCurrency.CORDOBA
        }
    }

    // Initialize initial database seeds if empty
    suspend fun initializeDefaultsIfEmpty() {
        // Seed default prices
        MealType.entries.forEach { meal ->
            mealPriceDao.insertOrUpdate(
                MealPriceEntity(
                    mealType = meal.name,
                    price = meal.defaultPrice
                )
            )
        }

        // Set default active currency if not set
        settingDao.setSetting(
            AppSettingEntity(
                key = KEY_ACTIVE_CURRENCY_CODE,
                value = AppCurrency.CORDOBA.code
            )
        )
    }

    // Insert a new transaction (Meal or Payment)
    suspend fun addTransaction(transaction: Transaction) {
        transactionDao.insert(TransactionEntity.fromDomain(transaction))
    }

    // Delete a transaction
    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(TransactionEntity.fromDomain(transaction))
    }

    // Update price of a meal type
    suspend fun updateMealPrice(mealType: MealType, price: Double) {
        mealPriceDao.insertOrUpdate(
            MealPriceEntity(
                mealType = mealType.name,
                price = price
            )
        )
    }

    // Set active currency code
    suspend fun setActiveCurrency(currency: AppCurrency) {
        settingDao.setSetting(
            AppSettingEntity(
                key = KEY_ACTIVE_CURRENCY_CODE,
                value = currency.code
            )
        )
    }

    // Save a custom currency
    suspend fun addCustomCurrency(currency: AppCurrency) {
        customCurrencyDao.insertCustomCurrency(
            CustomCurrencyEntity.fromDomain(currency)
        )
        setActiveCurrency(currency)
    }

    // Reset data to fresh demo state
    suspend fun resetToDemoData() {
        transactionDao.clearAll()
        customCurrencyDao.clearAll()

        // Reset default prices
        MealType.entries.forEach { meal ->
            mealPriceDao.insertOrUpdate(
                MealPriceEntity(
                    mealType = meal.name,
                    price = meal.defaultPrice
                )
            )
        }

        // Reset active currency to NIO
        setActiveCurrency(AppCurrency.CORDOBA)

        // Seed demo transactions
        val now = System.currentTimeMillis()
        val demoTransactions = listOf(
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

        demoTransactions.forEach { addTransaction(it) }
    }

    companion object {
        private const val KEY_ACTIVE_CURRENCY_CODE = "active_currency_code"
    }
}
