package com.hectormeza.comidas.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hectormeza.comidas.data.local.ComidasDatabaseHelper
import com.hectormeza.comidas.data.repository.ComidasRepository
import com.hectormeza.comidas.model.AppCurrency
import com.hectormeza.comidas.model.MealType
import com.hectormeza.comidas.model.Transaction
import com.hectormeza.comidas.model.TransactionType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class ComidasUiState(
    val transactions: List<Transaction> = emptyList(),
    val mealPrices: Map<MealType, Double> = mapOf(
        MealType.BREAKFAST to MealType.BREAKFAST.defaultPrice,
        MealType.LUNCH to MealType.LUNCH.defaultPrice,
        MealType.DINNER to MealType.DINNER.defaultPrice
    ),
    val currentCurrency: AppCurrency = AppCurrency.USD,
    val availableCurrencies: List<AppCurrency> = AppCurrency.PREDEFINED_CURRENCIES,
    val hasCompletedOnboarding: Boolean = false,
    val isLoading: Boolean = false
) {
    val totalMealsAmount: Double
        get() = transactions.filter { it.type == TransactionType.MEAL }.sumOf { it.amount }

    val totalPaymentsAmount: Double
        get() = transactions.filter { it.type == TransactionType.PAYMENT }.sumOf { it.amount }

    val totalMealsCount: Int
        get() = transactions.filter { it.type == TransactionType.MEAL }.sumOf { it.quantity }

    val totalDebt: Double
        get() = (totalMealsAmount - totalPaymentsAmount).coerceAtLeast(0.0)
}

class ComidasViewModel(
    application: Application,
    private val repository: ComidasRepository
) : AndroidViewModel(application) {

    val uiState: StateFlow<ComidasUiState> = combine(
        repository.transactions,
        repository.mealPrices,
        repository.activeCurrency,
        repository.availableCurrencies,
        repository.hasCompletedOnboarding
    ) { transactions, prices, activeCurrency, availableCurrencies, hasCompletedOnboarding ->
        ComidasUiState(
            transactions = transactions,
            mealPrices = prices,
            currentCurrency = activeCurrency,
            availableCurrencies = availableCurrencies,
            hasCompletedOnboarding = hasCompletedOnboarding,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ComidasUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            repository.initialize()
        }
    }

    // Complete onboarding guide
    fun completeOnboarding() {
        viewModelScope.launch {
            repository.completeOnboarding()
        }
    }

    // Show onboarding guide again from settings
    fun showOnboardingGuide() {
        viewModelScope.launch {
            repository.showOnboardingAgain()
        }
    }

    // Register a meal with quantity
    fun addMeal(mealType: MealType, quantity: Int) {
        viewModelScope.launch {
            val currentPrices = uiState.value.mealPrices
            val unitPrice = currentPrices[mealType] ?: mealType.defaultPrice
            val totalAmount = unitPrice * quantity
            val newTx = Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.MEAL,
                amount = totalAmount,
                mealType = mealType,
                quantity = quantity,
                timestamp = System.currentTimeMillis()
            )
            repository.addTransaction(newTx)
        }
    }

    // Register a past meal
    fun addPastMeal(mealType: MealType, quantity: Int, timestamp: Long, note: String?) {
        viewModelScope.launch {
            val currentPrices = uiState.value.mealPrices
            val unitPrice = currentPrices[mealType] ?: mealType.defaultPrice
            val totalAmount = unitPrice * quantity
            val newTx = Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.MEAL,
                amount = totalAmount,
                mealType = mealType,
                quantity = quantity,
                note = note?.ifBlank { null },
                timestamp = timestamp
            )
            repository.addTransaction(newTx)
        }
    }

    // Register a payment/abono
    fun addPayment(amount: Double, note: String?) {
        viewModelScope.launch {
            val newTx = Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.PAYMENT,
                amount = amount,
                note = note?.ifBlank { null },
                timestamp = System.currentTimeMillis()
            )
            repository.addTransaction(newTx)
        }
    }

    // Update meal price in database
    fun updateMealPrice(mealType: MealType, price: Double) {
        viewModelScope.launch {
            repository.updateMealPrice(mealType, price)
        }
    }

    // Select active currency
    fun selectCurrency(currency: AppCurrency) {
        viewModelScope.launch {
            repository.setActiveCurrency(currency)
        }
    }

    // Add and select a new custom currency
    fun addCustomCurrency(currency: AppCurrency) {
        viewModelScope.launch {
            repository.addCustomCurrency(currency)
        }
    }

    // Reset database to clean initial state (No demo data)
    fun resetData() {
        viewModelScope.launch {
            repository.resetData()
        }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val dbHelper = ComidasDatabaseHelper(application)
                    val repository = ComidasRepository(dbHelper)
                    return ComidasViewModel(application, repository) as T
                }
            }
    }
}
