package com.hectormeza.comidas

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hectormeza.comidas.model.AppCurrency
import com.hectormeza.comidas.model.MealType
import com.hectormeza.comidas.ui.components.AddPastMealBottomSheet
import com.hectormeza.comidas.ui.components.AddPaymentBottomSheet
import com.hectormeza.comidas.ui.components.FloatingBottomBar
import com.hectormeza.comidas.ui.components.ModernTab
import com.hectormeza.comidas.ui.screens.HistoryScreen
import com.hectormeza.comidas.ui.screens.HomeScreen
import com.hectormeza.comidas.ui.screens.OnboardingScreen
import com.hectormeza.comidas.ui.screens.SettingsScreen
import com.hectormeza.comidas.ui.screens.SplashScreen
import com.hectormeza.comidas.ui.theme.ComidasTheme
import com.hectormeza.comidas.ui.theme.WarmBackground
import com.hectormeza.comidas.ui.viewmodel.ComidasViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComidasTheme {
                val viewModel: ComidasViewModel = viewModel(
                    factory = ComidasViewModel.provideFactory(LocalContext.current.applicationContext as Application)
                )
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                        // If user hasn't completed onboarding, show welcome guide
                        if (!uiState.hasCompletedOnboarding && !uiState.isLoading) {
                            OnboardingScreen(
                                currentCurrency = uiState.currentCurrency,
                                availableCurrencies = uiState.availableCurrencies,
                                mealPrices = uiState.mealPrices,
                                onCurrencySelected = { viewModel.selectCurrency(it) },
                                onUpdateMealPrice = { mealType, price ->
                                    viewModel.updateMealPrice(mealType, price)
                                },
                                onFinishOnboarding = {
                                    viewModel.completeOnboarding()
                                }
                            )
                        } else {
                            ComidasMainApp(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComidasMainApp(
    viewModel: ComidasViewModel
) {
    var currentTab by remember { mutableStateOf(ModernTab.HOME) }
    var showPaymentSheet by remember { mutableStateOf(false) }
    var showPastMealSheet by remember { mutableStateOf(false) }

    val paymentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val pastMealSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Collect reactive state from SQLite DB via ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                            totalDebt = uiState.totalDebt,
                            totalMealsCount = uiState.totalMealsCount,
                            totalPaymentsAmount = uiState.totalPaymentsAmount,
                            currency = uiState.currentCurrency,
                            mealPrices = uiState.mealPrices,
                            recentTransactions = uiState.transactions,
                            onAddMealWithQuantity = { mealType, qty ->
                                viewModel.addMeal(mealType, qty)
                                val unitPrice = uiState.mealPrices[mealType] ?: mealType.defaultPrice
                                val total = unitPrice * qty
                                val title = if (qty > 1) "$qty× ${mealType.displayName}" else mealType.displayName
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "¡$title registrado! (+${uiState.currentCurrency.format(total)})",
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
                            transactions = uiState.transactions,
                            currency = uiState.currentCurrency
                        )
                    }

                    ModernTab.SETTINGS -> {
                        SettingsScreen(
                            currentCurrency = uiState.currentCurrency,
                            availableCurrencies = uiState.availableCurrencies,
                            onCurrencySelected = { newCurrency ->
                                viewModel.selectCurrency(newCurrency)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Moneda cambiada a ${newCurrency.displayName} (${newCurrency.symbol})",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            onAddCustomCurrency = { customCurrency ->
                                viewModel.addCustomCurrency(customCurrency)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Moneda personalizada guardada: ${customCurrency.displayName} (${customCurrency.symbol})",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            mealPrices = uiState.mealPrices,
                            onUpdateMealPrice = { mealType, newPrice ->
                                viewModel.updateMealPrice(mealType, newPrice)
                            },
                            onShowWelcomeGuide = {
                                viewModel.showOnboardingGuide()
                            },
                            onResetDemoData = {
                                viewModel.resetData()
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Valores restablecidos por defecto")
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
                    currentDebt = uiState.totalDebt,
                    currency = uiState.currentCurrency,
                    onDismiss = {
                        showPaymentSheet = false
                    },
                    onConfirmPayment = { amount, note ->
                        viewModel.addPayment(amount, note)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Abono de ${uiState.currentCurrency.format(amount)} guardado",
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
                    currency = uiState.currentCurrency,
                    mealPrices = uiState.mealPrices,
                    onDismiss = {
                        showPastMealSheet = false
                    },
                    onConfirmPastMeal = { mealType, quantity, timestamp, note ->
                        viewModel.addPastMeal(mealType, quantity, timestamp, note)
                        val unitPrice = uiState.mealPrices[mealType] ?: mealType.defaultPrice
                        val totalAmount = unitPrice * quantity
                        val title = if (quantity > 1) "$quantity× ${mealType.displayName}" else mealType.displayName
                        val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
                        val dateStr = sdf.format(Date(timestamp))

                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "¡$title del $dateStr guardado! (+${uiState.currentCurrency.format(totalAmount)})",
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
        // Preview dummy
    }
}