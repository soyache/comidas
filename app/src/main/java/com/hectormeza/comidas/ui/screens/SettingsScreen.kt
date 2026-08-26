package com.hectormeza.comidas.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material.icons.rounded.LunchDining
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.hectormeza.comidas.model.AppCurrency
import com.hectormeza.comidas.model.MealType
import com.hectormeza.comidas.ui.theme.BreakfastAccent
import com.hectormeza.comidas.ui.theme.BurgundyDark
import com.hectormeza.comidas.ui.theme.CardBorder
import com.hectormeza.comidas.ui.theme.ComidasTheme
import com.hectormeza.comidas.ui.theme.DinnerAccent
import com.hectormeza.comidas.ui.theme.LunchAccent
import com.hectormeza.comidas.ui.theme.SoftCardBg
import com.hectormeza.comidas.ui.theme.TextPrimary
import com.hectormeza.comidas.ui.theme.TextSecondary
import com.hectormeza.comidas.ui.theme.WarmBackground
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    currentCurrency: AppCurrency,
    availableCurrencies: List<AppCurrency>,
    onCurrencySelected: (AppCurrency) -> Unit,
    onAddCustomCurrency: (AppCurrency) -> Unit,
    mealPrices: Map<MealType, Double>,
    onUpdateMealPrice: (MealType, Double) -> Unit,
    onShowWelcomeGuide: () -> Unit,
    onResetDemoData: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCustomCurrencyDialog by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Smooth scroll interpolation factor
    val scrollFraction by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex > 0) 1f
            else (listState.firstVisibleItemScrollOffset / 100f).coerceIn(0f, 1f)
        }
    }

    val titleSize = lerp(19.sp, 15.sp, scrollFraction)
    val barHeight = lerp(48.dp, 40.dp, scrollFraction)

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 52.dp, // Space for collapsing top bar
                bottom = 96.dp // Space for floating bottom bar
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Currency Selection Section
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.CurrencyExchange,
                        contentDescription = null,
                        tint = BurgundyDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TIPO DE MONEDA",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftCardBg),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "Seleccioná o agregá tu moneda personalizada:",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableCurrencies.forEach { currency ->
                                val isSelected = currentCurrency.code == currency.code && currentCurrency.symbol == currency.symbol
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onCurrencySelected(currency) },
                                    leadingIcon = if (isSelected) {
                                        {
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = Color.White
                                            )
                                        }
                                    } else null,
                                    label = {
                                        Text(
                                            text = "${currency.flagEmoji} ${currency.code} (${currency.symbol})",
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BurgundyDark,
                                        selectedLabelColor = Color.White,
                                        containerColor = Color.White,
                                        labelColor = TextPrimary
                                    ),
                                    border = if (isSelected) null else BorderStroke(1.dp, CardBorder)
                                )
                            }

                            // Add Custom Currency Action Chip
                            FilterChip(
                                selected = false,
                                onClick = { showCustomCurrencyDialog = true },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = BurgundyDark
                                    )
                                },
                                label = {
                                    Text(
                                        text = "Personalizada",
                                        color = BurgundyDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, BurgundyDark.copy(alpha = 0.5f))
                            )
                        }
                    }
                }
            }

            // 2. Price Configuration Section Header
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Sell,
                        contentDescription = null,
                        tint = BurgundyDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PRECIOS POR TIEMPO DE COMIDA",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Breakfast Price Card
            item {
                MealPriceSettingCard(
                    mealType = MealType.BREAKFAST,
                    currentPrice = mealPrices[MealType.BREAKFAST] ?: MealType.BREAKFAST.defaultPrice,
                    currency = currentCurrency,
                    icon = Icons.Rounded.WbSunny,
                    accentColor = BreakfastAccent,
                    onPriceChanged = { onUpdateMealPrice(MealType.BREAKFAST, it) }
                )
            }

            // Lunch Price Card
            item {
                MealPriceSettingCard(
                    mealType = MealType.LUNCH,
                    currentPrice = mealPrices[MealType.LUNCH] ?: MealType.LUNCH.defaultPrice,
                    currency = currentCurrency,
                    icon = Icons.Rounded.LunchDining,
                    accentColor = LunchAccent,
                    onPriceChanged = { onUpdateMealPrice(MealType.LUNCH, it) }
                )
            }

            // Dinner Price Card
            item {
                MealPriceSettingCard(
                    mealType = MealType.DINNER,
                    currentPrice = mealPrices[MealType.DINNER] ?: MealType.DINNER.defaultPrice,
                    currency = currentCurrency,
                    icon = Icons.Rounded.Bedtime,
                    accentColor = DinnerAccent,
                    onPriceChanged = { onUpdateMealPrice(MealType.DINNER, it) }
                )
            }

            // 3. Detection Rules Information Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftCardBg),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.AccessTime,
                                contentDescription = null,
                                tint = BurgundyDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Detección Automática por Horario",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "La aplicación detecta el tiempo de comida según la hora en la que se registra la transacción:\n\n" +
                                    "• Desayuno: 06:00 a 11:59\n" +
                                    "• Almuerzo: 12:00 a 17:59\n" +
                                    "• Cena: 18:00 a 05:59\n\n" +
                                    "También podés cambiar manualmente el tiempo antes de registrar o agregar comidas de días anteriores.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // 4. Welcome Guide Action Button
            item {
                Button(
                    onClick = onShowWelcomeGuide,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BurgundyDark)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AccessTime,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ver Guía de Bienvenida",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // 5. Reset Data
            item {
                OutlinedButton(
                    onClick = onResetDemoData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = null,
                        tint = BurgundyDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Restablecer a Valores por Defecto",
                        color = BurgundyDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Pinned Collapsing Top App Bar (Zero shadows, ultra-minimal height)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            color = WarmBackground.copy(alpha = if (scrollFraction > 0.1f) 0.98f else 1f),
            shadowElevation = 0.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(barHeight)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AJUSTES",
                        fontSize = titleSize,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        color = BurgundyDark
                    )
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

    // Custom Currency Dialog
    if (showCustomCurrencyDialog) {
        AddCustomCurrencyDialog(
            onDismiss = { showCustomCurrencyDialog = false },
            onConfirm = { customCurrency ->
                onAddCustomCurrency(customCurrency)
                onCurrencySelected(customCurrency)
                showCustomCurrencyDialog = false
            }
        )
    }
}

@Composable
private fun AddCustomCurrencyDialog(
    onDismiss: () -> Unit,
    onConfirm: (AppCurrency) -> Unit
) {
    var symbolInput by remember { mutableStateOf("") }
    var codeInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }

    val isValid = symbolInput.isNotBlank() && codeInput.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Nueva Moneda Personalizada",
                fontWeight = FontWeight.Bold,
                color = BurgundyDark
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Ingresá el símbolo y código de tu moneda local.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = symbolInput,
                    onValueChange = { symbolInput = it.take(6) },
                    label = { Text("Símbolo (ej. Q, L, S/, Bs.)") },
                    placeholder = { Text("ej. Q") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = codeInput,
                    onValueChange = { codeInput = it.uppercase().take(5) },
                    label = { Text("Código / Abreviatura (ej. GTQ, BOB)") },
                    placeholder = { Text("ej. GTQ") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Nombre (opcional)") },
                    placeholder = { Text("ej. Quetzal guatemalteco") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (isValid) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SoftCardBg,
                        border = BorderStroke(1.dp, CardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Vista previa: ${symbolInput.trim()} 130.00",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = BurgundyDark,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isValid) {
                        val newCurrency = AppCurrency(
                            code = codeInput.trim().uppercase(),
                            symbol = symbolInput.trim(),
                            displayName = nameInput.ifBlank { codeInput.trim().uppercase() },
                            flagEmoji = "🪙",
                            isCustom = true
                        )
                        onConfirm(newCurrency)
                    }
                },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(containerColor = BurgundyDark)
            ) {
                Text("Guardar y Usar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun MealPriceSettingCard(
    mealType: MealType,
    currentPrice: Double,
    currency: AppCurrency,
    icon: ImageVector,
    accentColor: Color,
    onPriceChanged: (Double) -> Unit
) {
    var textValue by remember(currentPrice) {
        mutableStateOf(String.format(Locale.US, "%.2f", currentPrice))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SoftCardBg),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = mealType.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = mealType.timeRangeDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            // Price input
            OutlinedTextField(
                value = textValue,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("""^\d*\.?\d{0,2}$"""))) {
                        textValue = input
                        val parsed = input.toDoubleOrNull()
                        if (parsed != null && parsed >= 0) {
                            onPriceChanged(parsed)
                        }
                    }
                },
                prefix = { Text("${currency.symbol} ", fontWeight = FontWeight.Bold, color = BurgundyDark) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.width(125.dp),
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    ComidasTheme {
        SettingsScreen(
            currentCurrency = AppCurrency.USD,
            availableCurrencies = AppCurrency.PREDEFINED_CURRENCIES,
            onCurrencySelected = {},
            onAddCustomCurrency = {},
            mealPrices = mapOf(
                MealType.BREAKFAST to 3.0,
                MealType.LUNCH to 5.0,
                MealType.DINNER to 4.0
            ),
            onUpdateMealPrice = { _, _ -> },
            onShowWelcomeGuide = {},
            onResetDemoData = {}
        )
    }
}
