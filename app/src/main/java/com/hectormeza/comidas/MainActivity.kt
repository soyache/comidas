package com.hectormeza.comidas

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hectormeza.comidas.model.AppCurrency
import com.hectormeza.comidas.model.MealType
import com.hectormeza.comidas.model.Transaction
import com.hectormeza.comidas.ui.components.AddPastMealBottomSheet
import com.hectormeza.comidas.ui.components.AddPaymentBottomSheet
import com.hectormeza.comidas.ui.components.EditTransactionBottomSheet
import com.hectormeza.comidas.ui.components.FloatingBottomBar
import com.hectormeza.comidas.ui.components.ModernTab
import com.hectormeza.comidas.ui.screens.HistoryScreen
import com.hectormeza.comidas.ui.screens.HomeScreen
import com.hectormeza.comidas.ui.screens.OnboardingScreen
import com.hectormeza.comidas.ui.screens.SettingsScreen
import com.hectormeza.comidas.ui.screens.SplashScreen
import com.hectormeza.comidas.ui.theme.BurgundyDark
import com.hectormeza.comidas.ui.theme.ComidasTheme
import com.hectormeza.comidas.ui.theme.CoralAccent
import com.hectormeza.comidas.ui.theme.TextPrimary
import com.hectormeza.comidas.ui.theme.TextSecondary
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
                                onAddCustomCurrency = { viewModel.addCustomCurrency(it) },
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

    var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }

    val paymentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val pastMealSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                            onEditTransaction = { tx ->
                                transactionToEdit = tx
                            },
                            onDeleteTransaction = { tx ->
                                transactionToDelete = tx
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
                            currency = uiState.currentCurrency,
                            onEditTransaction = { tx ->
                                transactionToEdit = tx
                            },
                            onDeleteTransaction = { tx ->
                                transactionToDelete = tx
                            }
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

            // Edit Transaction BottomSheet
            transactionToEdit?.let { tx ->
                EditTransactionBottomSheet(
                    sheetState = editSheetState,
                    transaction = tx,
                    currency = uiState.currentCurrency,
                    mealPrices = uiState.mealPrices,
                    onDismiss = {
                        transactionToEdit = null
                    },
                    onConfirmEdit = { updatedTx ->
                        viewModel.updateTransaction(updatedTx)
                        transactionToEdit = null
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Movimiento actualizado correctamente",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                )
            }

            // Delete Confirmation Dialog
            transactionToDelete?.let { tx ->
                val isMeal = tx.type == com.hectormeza.comidas.model.TransactionType.MEAL
                AlertDialog(
                    onDismissRequest = { transactionToDelete = null },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = null,
                            tint = CoralAccent,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    title = {
                        Text(
                            text = "¿Eliminar este movimiento?",
                            fontWeight = FontWeight.Bold,
                            color = BurgundyDark,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = "${tx.displayTitle} • ${uiState.currentCurrency.format(tx.amount)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Esta acción eliminará el registro y actualizará automáticamente tu balance pendiente.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val toDelete = tx
                                viewModel.deleteTransaction(toDelete)
                                transactionToDelete = null
                                coroutineScope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Movimiento eliminado",
                                        actionLabel = "Deshacer",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.updateTransaction(toDelete)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CoralAccent,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Eliminar", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { transactionToDelete = null },
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Cancelar", color = TextSecondary, fontWeight = FontWeight.Bold)
                        }
                    }
                )
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
                    onConfirmPastMeals = { mealTypes, quantity, timestamp, note ->
                        viewModel.addPastMeals(mealTypes, quantity, timestamp, note)
                        val totalAmount = mealTypes.sumOf { mealType ->
                            (uiState.mealPrices[mealType] ?: mealType.defaultPrice) * quantity
                        }
                        val title = if (mealTypes.size == 1) {
                            val mealType = mealTypes.first()
                            if (quantity > 1) "$quantity× ${mealType.displayName}" else mealType.displayName
                        } else {
                            mealTypes.joinToString(" + ") { it.displayName }
                        }
                        val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
                        val dateStr = sdf.format(Date(timestamp))
                        val savedWord = if (mealTypes.size > 1) "guardados" else "guardado"

                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "¡$title del $dateStr $savedWord! (+${uiState.currentCurrency.format(totalAmount)})",
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