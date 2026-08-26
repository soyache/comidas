package com.hectormeza.comidas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.hectormeza.comidas.model.AppCurrency
import com.hectormeza.comidas.model.MealType
import com.hectormeza.comidas.model.Transaction
import com.hectormeza.comidas.model.TransactionType
import com.hectormeza.comidas.ui.components.AddPastMealBottomSheet
import com.hectormeza.comidas.ui.components.AddPaymentBottomSheet
import com.hectormeza.comidas.ui.components.FloatingBottomBar
import com.hectormeza.comidas.ui.components.ModernTab
import com.hectormeza.comidas.ui.screens.HistoryScreen
import com.hectormeza.comidas.ui.screens.HomeScreen
import com.hectormeza.comidas.ui.screens.SettingsScreen
import com.hectormeza.comidas.ui.screens.SplashScreen
import com.hectormeza.comidas.ui.theme.ComidasTheme
import com.hectormeza.comidas.ui.theme.WarmBackground
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComidasTheme {
                var showSplash by remember { mutableStateOf(true) }

                Crossfade(
                    targetState = showSplash,
                    label = "splashTransition"
                ) { isSplash ->
                    if (isSplash) {
                        SplashScreen(
                            onSplashFinished = { showSplash = false }
                        )
                    } else {
                        ComidasMainApp()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComidasMainApp() {
    var currentTab by remember { mutableStateOf(ModernTab.HOME) }
    val availableCurrencies = remember {
        mutableStateListOf(*AppCurrency.PREDEFINED_CURRENCIES.toTypedArray())
    }
    var currentCurrency by remember { mutableStateOf(AppCurrency.CORDOBA) }
    var showPaymentSheet by remember { mutableStateOf(false) }
    var showPastMealSheet by remember { mutableStateOf(false) }

    val paymentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val pastMealSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Configured Prices
    val mealPrices = remember {
        mutableStateMapOf(
            MealType.BREAKFAST to MealType.BREAKFAST.defaultPrice,
            MealType.LUNCH to MealType.LUNCH.defaultPrice,
            MealType.DINNER to MealType.DINNER.defaultPrice
        )
    }

    // Mock initial transactions for interactive UI testing
    val transactions = remember {
        mutableStateListOf(
            Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.MEAL,
                amount = 130.0,
                mealType = MealType.LUNCH,
                quantity = 1,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 3
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.MEAL,
                amount = 80.0,
                mealType = MealType.BREAKFAST,
                quantity = 1,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 7
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.PAYMENT,
                amount = 100.0,
                note = "Abono quincenal",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.MEAL,
                amount = 100.0,
                mealType = MealType.DINNER,
                quantity = 1,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 28
            )
        )
    }

    // Derived Calculations
    val totalMealsAmount by remember {
        derivedStateOf {
            transactions.filter { it.type == TransactionType.MEAL }.sumOf { it.amount }
        }
    }

    val totalPaymentsAmount by remember {
        derivedStateOf {
            transactions.filter { it.type == TransactionType.PAYMENT }.sumOf { it.amount }
        }
    }

    val totalMealsCount by remember {
        derivedStateOf {
            transactions.filter { it.type == TransactionType.MEAL }.sumOf { it.quantity }
        }
    }

    val totalDebt by remember {
        derivedStateOf {
            (totalMealsAmount - totalPaymentsAmount).coerceAtLeast(0.0)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = WarmBackground,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Crossfade(
                targetState = currentTab,
                label = "tabTransition",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) { tab ->
                when (tab) {
                    ModernTab.HOME -> {
                        HomeScreen(
                            totalDebt = totalDebt,
                            totalMealsCount = totalMealsCount,
                            totalPaymentsAmount = totalPaymentsAmount,
                            currency = currentCurrency,
                            mealPrices = mealPrices,
                            recentTransactions = transactions,
                            onAddMealWithQuantity = { mealType, qty ->
                                val unitPrice = mealPrices[mealType] ?: mealType.defaultPrice
                                val totalAmount = unitPrice * qty
                                val newTx = Transaction(
                                    id = UUID.randomUUID().toString(),
                                    type = TransactionType.MEAL,
                                    amount = totalAmount,
                                    mealType = mealType,
                                    quantity = qty,
                                    timestamp = System.currentTimeMillis()
                                )
                                transactions.add(0, newTx)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "¡${newTx.displayTitle} registrado! (+${currentCurrency.format(totalAmount)})",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            onOpenPaymentSheet = {
                                showPaymentSheet = true
                            },
                            onOpenPastMealSheet = {
                                showPastMealSheet = true
                            },
                            onNavigateToHistory = {
                                currentTab = ModernTab.HISTORY
                            }
                        )
                    }

                    ModernTab.HISTORY -> {
                        HistoryScreen(
                            transactions = transactions,
                            currency = currentCurrency
                        )
                    }

                    ModernTab.SETTINGS -> {
                        SettingsScreen(
                            currentCurrency = currentCurrency,
                            availableCurrencies = availableCurrencies,
                            onCurrencySelected = { newCurrency ->
                                currentCurrency = newCurrency
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Moneda cambiada a ${newCurrency.displayName} (${newCurrency.symbol})",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            onAddCustomCurrency = { customCurrency ->
                                if (!availableCurrencies.any { it.code == customCurrency.code && it.symbol == customCurrency.symbol }) {
                                    availableCurrencies.add(customCurrency)
                                }
                                currentCurrency = customCurrency
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Moneda personalizada agregada: ${customCurrency.displayName} (${customCurrency.symbol})",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            mealPrices = mealPrices,
                            onUpdateMealPrice = { mealType, newPrice ->
                                mealPrices[mealType] = newPrice
                            },
                            onResetDemoData = {
                                transactions.clear()
                                mealPrices[MealType.BREAKFAST] = MealType.BREAKFAST.defaultPrice
                                mealPrices[MealType.LUNCH] = MealType.LUNCH.defaultPrice
                                mealPrices[MealType.DINNER] = MealType.DINNER.defaultPrice
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Datos de demostración restablecidos")
                                }
                            }
                        )
                    }
                }
            }

            // Payment BottomSheet
            if (showPaymentSheet) {
                AddPaymentBottomSheet(
                    sheetState = paymentSheetState,
                    currentDebt = totalDebt,
                    currency = currentCurrency,
                    onDismiss = {
                        showPaymentSheet = false
                    },
                    onConfirmPayment = { amount, note ->
                        val newTx = Transaction(
                            id = UUID.randomUUID().toString(),
                            type = TransactionType.PAYMENT,
                            amount = amount,
                            note = note.ifBlank { null },
                            timestamp = System.currentTimeMillis()
                        )
                        val index = transactions.indexOfFirst { it.timestamp <= newTx.timestamp }.let { if (it == -1) transactions.size else it }
                        transactions.add(index, newTx)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Abono de ${currentCurrency.format(amount)} registrado",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                )
            }

            // Past Meal BottomSheet
            if (showPastMealSheet) {
                AddPastMealBottomSheet(
                    sheetState = pastMealSheetState,
                    currency = currentCurrency,
                    mealPrices = mealPrices,
                    onDismiss = {
                        showPastMealSheet = false
                    },
                    onConfirmPastMeal = { mealType, quantity, timestamp, note ->
                        val unitPrice = mealPrices[mealType] ?: mealType.defaultPrice
                        val totalAmount = unitPrice * quantity
                        val newTx = Transaction(
                            id = UUID.randomUUID().toString(),
                            type = TransactionType.MEAL,
                            amount = totalAmount,
                            mealType = mealType,
                            quantity = quantity,
                            note = note,
                            timestamp = timestamp
                        )
                        val index = transactions.indexOfFirst { it.timestamp <= newTx.timestamp }.let { if (it == -1) transactions.size else it }
                        transactions.add(index, newTx)

                        val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
                        val dateStr = sdf.format(Date(timestamp))
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "¡${newTx.displayTitle} del $dateStr registrado! (+${currentCurrency.format(totalAmount)})",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                )
            }
        }

        // Floating Bottom Bar purely overlaid with transparent background
        FloatingBottomBar(
            currentTab = currentTab,
            onTabSelected = { currentTab = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ComidasMainAppPreview() {
    ComidasTheme {
        ComidasMainApp()
    }
}