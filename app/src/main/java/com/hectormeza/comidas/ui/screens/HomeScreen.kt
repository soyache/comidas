package com.hectormeza.comidas.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.hectormeza.comidas.model.AppCurrency
import com.hectormeza.comidas.model.MealType
import com.hectormeza.comidas.model.Transaction
import com.hectormeza.comidas.model.TransactionType
import com.hectormeza.comidas.ui.components.BurgerCalculatorLogo
import com.hectormeza.comidas.ui.components.ConfirmMealDialog
import com.hectormeza.comidas.ui.components.FloatingBalanceDock
import com.hectormeza.comidas.ui.components.HeroMealShowcase
import com.hectormeza.comidas.ui.components.TransactionItemRow
import com.hectormeza.comidas.ui.theme.BurgundyDark
import com.hectormeza.comidas.ui.theme.CardBorder
import com.hectormeza.comidas.ui.theme.ComidasTheme
import com.hectormeza.comidas.ui.theme.SoftCardBg
import com.hectormeza.comidas.ui.theme.TextPrimary
import com.hectormeza.comidas.ui.theme.TextSecondary
import com.hectormeza.comidas.ui.theme.WarmBackground
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    totalDebt: Double,
    totalMealsCount: Int,
    totalPaymentsAmount: Double,
    currency: AppCurrency,
    mealPrices: Map<MealType, Double>,
    recentTransactions: List<Transaction>,
    onAddMealWithQuantity: (MealType, Int) -> Unit,
    onOpenPaymentSheet: () -> Unit,
    onOpenPastMealSheet: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val detectedMealType = remember { MealType.detectCurrent() }
    val todayDateStr = remember {
        val sdf = SimpleDateFormat("dd 'DE' MMMM", Locale.getDefault())
        sdf.format(Date()).uppercase()
    }

    var showConfirmDialog by remember { mutableStateOf(false) }

    val currentPrice = mealPrices[detectedMealType] ?: detectedMealType.defaultPrice
    val listState = rememberLazyListState()

    // Smooth scroll interpolation factor [0f = top/expanded, 1f = scrolled/collapsed]
    val scrollFraction by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex > 0) 1f
            else (listState.firstVisibleItemScrollOffset / 140f).coerceIn(0f, 1f)
        }
    }

    val logoSize = lerp(36.dp, 24.dp, scrollFraction)
    val titleSize = lerp(28.sp, 18.sp, scrollFraction)
    val actionBtnSize = lerp(44.dp, 36.dp, scrollFraction)
    val iconInsideSize = lerp(18.dp, 15.dp, scrollFraction)
    val barHeight = lerp(72.dp, 54.dp, scrollFraction)

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 86.dp, // Space for collapsing top bar
                bottom = 96.dp // Space for floating bottom bar
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Section Header: "TODAY'S RAIL" style layout
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "REGISTRO DEL DÍA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "$todayDateStr • DETECCIÓN: ${detectedMealType.displayName.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            letterSpacing = 0.8.sp
                        )
                    }

                    // Segmented icons pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SoftCardBg,
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(BurgundyDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.GridView,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Single Hero Meal Showcase (Centered Stage with hanging tag & clickable past date icon)
            item {
                HeroMealShowcase(
                    currentMealType = detectedMealType,
                    price = currentPrice,
                    currency = currency,
                    onShowcaseClick = { showConfirmDialog = true },
                    onPastDateClick = { onOpenPastMealSheet() }
                )
            }

            // 3. Floating Balance Dock (Reference "IN THE BAG" bar)
            item {
                FloatingBalanceDock(
                    totalDebt = totalDebt,
                    totalMealsCount = totalMealsCount,
                    totalPaymentsAmount = totalPaymentsAmount,
                    currency = currency,
                    onClick = onNavigateToHistory
                )
            }

            // 4. Recent Activity Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MOVIMIENTOS RECIENTES",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )

                    if (recentTransactions.isNotEmpty()) {
                        TextButton(onClick = onNavigateToHistory) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Ver todos",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = BurgundyDark,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = null,
                                    tint = BurgundyDark,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Recent items preview
            if (recentTransactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftCardBg),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ReceiptLong,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Aún no hay movimientos",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Tocá el área de comida de arriba para registrar tu primer tiempo.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            } else {
                items(recentTransactions.take(3), key = { it.id }) { transaction ->
                    TransactionItemRow(
                        transaction = transaction,
                        currency = currency
                    )
                }
            }
        }

        // Pinned Collapsing Animated Top App Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            color = WarmBackground.copy(alpha = (0.75f + (scrollFraction * 0.25f)).coerceIn(0f, 1f)),
            shadowElevation = if (scrollFraction > 0.15f) (scrollFraction * 6).dp else 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(barHeight)
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BurgerCalculatorLogo(size = logoSize)
                        Spacer(modifier = Modifier.width(lerp(8.dp, 6.dp, scrollFraction)))
                        Text(
                            text = "COMIDAS",
                            fontSize = titleSize,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp,
                            color = BurgundyDark
                        )
                    }

                    // Action buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Past meal button
                        Surface(
                            modifier = Modifier
                                .size(actionBtnSize)
                                .clip(CircleShape)
                                .clickable { onOpenPastMealSheet() },
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(1.dp, CardBorder),
                            shadowElevation = 1.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.CalendarToday,
                                    contentDescription = "Comida pasada",
                                    tint = BurgundyDark,
                                    modifier = Modifier.size(iconInsideSize)
                                )
                            }
                        }

                        // Abono quick button
                        Surface(
                            modifier = Modifier
                                .size(actionBtnSize)
                                .clip(CircleShape)
                                .clickable { onOpenPaymentSheet() },
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(1.dp, CardBorder),
                            shadowElevation = 1.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.Payment,
                                    contentDescription = "Abonar",
                                    tint = BurgundyDark,
                                    modifier = Modifier.size(iconInsideSize + 2.dp)
                                )
                            }
                        }
                    }
                }

                if (scrollFraction > 0.1f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(CardBorder.copy(alpha = scrollFraction))
                    )
                }
            }
        }
    }

    // Confirmation Modal Dialog
    if (showConfirmDialog) {
        ConfirmMealDialog(
            initialMealType = detectedMealType,
            mealPrices = mealPrices,
            currency = currency,
            onDismiss = { showConfirmDialog = false },
            onConfirm = { mealType, qty ->
                onAddMealWithQuantity(mealType, qty)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ComidasTheme {
        HomeScreen(
            totalDebt = 890.0,
            totalMealsCount = 8,
            totalPaymentsAmount = 300.0,
            currency = AppCurrency.CORDOBA,
            mealPrices = mapOf(
                MealType.BREAKFAST to 80.0,
                MealType.LUNCH to 130.0,
                MealType.DINNER to 100.0
            ),
            recentTransactions = listOf(
                Transaction(
                    id = "1",
                    type = TransactionType.MEAL,
                    amount = 130.0,
                    mealType = MealType.LUNCH
                ),
                Transaction(
                    id = "2",
                    type = TransactionType.PAYMENT,
                    amount = 200.0,
                    note = "Efectivo"
                )
            ),
            onAddMealWithQuantity = { _, _ -> },
            onOpenPaymentSheet = {},
            onOpenPastMealSheet = {},
            onNavigateToHistory = {}
        )
    }
}
