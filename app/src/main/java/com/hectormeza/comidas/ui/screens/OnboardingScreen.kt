package com.hectormeza.comidas.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LunchDining
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.PriceCheck
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hectormeza.comidas.model.AppCurrency
import com.hectormeza.comidas.model.MealType
import com.hectormeza.comidas.ui.components.AddCustomCurrencyDialog
import com.hectormeza.comidas.ui.components.BurgerCalculatorLogo
import com.hectormeza.comidas.ui.theme.BreakfastAccent
import com.hectormeza.comidas.ui.theme.BurgundyDark
import com.hectormeza.comidas.ui.theme.CardBorder
import com.hectormeza.comidas.ui.theme.ComidasTheme
import com.hectormeza.comidas.ui.theme.CoralAccent
import com.hectormeza.comidas.ui.theme.DinnerAccent
import com.hectormeza.comidas.ui.theme.LunchAccent
import com.hectormeza.comidas.ui.theme.MintPayment
import com.hectormeza.comidas.ui.theme.SoftCardBg
import com.hectormeza.comidas.ui.theme.TextPrimary
import com.hectormeza.comidas.ui.theme.TextSecondary
import com.hectormeza.comidas.ui.theme.WarmBackground
import java.util.Locale

import androidx.compose.foundation.layout.navigationBarsPadding

@Composable
fun OnboardingScreen(
    currentCurrency: AppCurrency,
    availableCurrencies: List<AppCurrency>,
    mealPrices: Map<MealType, Double>,
    onCurrencySelected: (AppCurrency) -> Unit,
    onAddCustomCurrency: (AppCurrency) -> Unit,
    onUpdateMealPrice: (MealType, Double) -> Unit,
    onFinishOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = 4

    Surface(
        modifier = modifier.fillMaxSize(),
        color = WarmBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Header: Logo + Step indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BurgerCalculatorLogo(size = 24.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "COMIDAS",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        color = BurgundyDark
                    )
                }

                TextButton(onClick = onFinishOnboarding) {
                    Text(
                        text = "Saltar",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }
            }

            // 2. Animated Content Body
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut()
                            )
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut()
                            )
                        }
                    },
                    label = "onboardingStepTransition"
                ) { step ->
                    when (step) {
                        0 -> OnboardingCurrencyStep(
                            currentCurrency = currentCurrency,
                            availableCurrencies = availableCurrencies,
                            onCurrencySelected = onCurrencySelected,
                            onAddCustomCurrency = onAddCustomCurrency
                        )
                        1 -> OnboardingPricesStep(
                            currency = currentCurrency,
                            mealPrices = mealPrices,
                            onUpdateMealPrice = onUpdateMealPrice
                        )
                        2 -> OnboardingAddMealStep(
                            currency = currentCurrency,
                            mealPrices = mealPrices
                        )
                        3 -> OnboardingPastMealsStep(
                            currency = currentCurrency,
                            mealPrices = mealPrices
                        )
                    }
                }
            }

            // 3. Bottom Controls (Indicators + Navigation Buttons)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Dot Indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(totalSteps) { index ->
                        val isSelected = currentStep == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(6.dp)
                                .width(if (isSelected) 24.dp else 6.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) BurgundyDark else CardBorder)
                        )
                    }
                }

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 0) {
                        TextButton(
                            onClick = { currentStep-- },
                            modifier = Modifier.height(50.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Atrás",
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Button(
                        onClick = {
                            if (currentStep < totalSteps - 1) {
                                currentStep++
                            } else {
                                onFinishOnboarding()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BurgundyDark,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .padding(start = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Text(
                                text = if (currentStep == totalSteps - 1) "¡Comenzar ahora!" else "Siguiente",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// STEP 1: Currency Selection
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OnboardingCurrencyStep(
    currentCurrency: AppCurrency,
    availableCurrencies: List<AppCurrency>,
    onCurrencySelected: (AppCurrency) -> Unit,
    onAddCustomCurrency: (AppCurrency) -> Unit
) {
    var showAddCustomCurrencyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.CurrencyExchange,
                contentDescription = null,
                tint = BurgundyDark,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "1. Seleccioná tu Moneda",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = BurgundyDark,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Elegí la moneda principal para llevar el control exacto de tus comidas y abonos diarios.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, start = 8.dp, end = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = SoftCardBg),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Moneda Activa: ${currentCurrency.displayName} (${currentCurrency.symbol})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = BurgundyDark
                )

                Spacer(modifier = Modifier.height(12.dp))

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

                    // Button "+ Otra moneda"
                    FilterChip(
                        selected = false,
                        onClick = { showAddCustomCurrencyDialog = true },
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
                                text = "Personalizar moneda",
                                fontWeight = FontWeight.Bold,
                                color = BurgundyDark
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.White,
                            labelColor = BurgundyDark
                        ),
                        border = BorderStroke(1.dp, BurgundyDark.copy(alpha = 0.35f))
                    )
                }
            }
        }

        if (showAddCustomCurrencyDialog) {
            AddCustomCurrencyDialog(
                onDismiss = { showAddCustomCurrencyDialog = false },
                onConfirm = { newCurrency ->
                    onAddCustomCurrency(newCurrency)
                    onCurrencySelected(newCurrency)
                    showAddCustomCurrencyDialog = false
                }
            )
        }
    }
}

// STEP 2: Prices Configuration
@Composable
private fun OnboardingPricesStep(
    currency: AppCurrency,
    mealPrices: Map<MealType, Double>,
    onUpdateMealPrice: (MealType, Double) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.PriceCheck,
                contentDescription = null,
                tint = BurgundyDark,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "2. Precios por Tiempo",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = BurgundyDark,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Configurá el costo habitual de cada tiempo. Podés editarlos en cualquier momento desde Ajustes.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, start = 8.dp, end = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OnboardingPriceInputRow(
                mealType = MealType.BREAKFAST,
                price = mealPrices[MealType.BREAKFAST] ?: MealType.BREAKFAST.defaultPrice,
                currency = currency,
                icon = Icons.Rounded.WbSunny,
                accentColor = BreakfastAccent,
                onPriceChanged = { onUpdateMealPrice(MealType.BREAKFAST, it) }
            )

            OnboardingPriceInputRow(
                mealType = MealType.LUNCH,
                price = mealPrices[MealType.LUNCH] ?: MealType.LUNCH.defaultPrice,
                currency = currency,
                icon = Icons.Rounded.LunchDining,
                accentColor = LunchAccent,
                onPriceChanged = { onUpdateMealPrice(MealType.LUNCH, it) }
            )

            OnboardingPriceInputRow(
                mealType = MealType.DINNER,
                price = mealPrices[MealType.DINNER] ?: MealType.DINNER.defaultPrice,
                currency = currency,
                icon = Icons.Rounded.Bedtime,
                accentColor = DinnerAccent,
                onPriceChanged = { onUpdateMealPrice(MealType.DINNER, it) }
            )
        }
    }
}

@Composable
private fun OnboardingPriceInputRow(
    mealType: MealType,
    price: Double,
    currency: AppCurrency,
    icon: ImageVector,
    accentColor: Color,
    onPriceChanged: (Double) -> Unit
) {
    var textValue by remember(price) {
        mutableStateOf(String.format(Locale.US, "%.2f", price))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SoftCardBg),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = mealType.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = mealType.timeRangeDescription,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

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
                prefix = { Text("${currency.symbol} ", fontWeight = FontWeight.Black, color = BurgundyDark) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.width(115.dp),
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black, color = TextPrimary),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = BurgundyDark,
                    unfocusedBorderColor = CardBorder
                )
            )
        }
    }
}

// STEP 3: How to register today's meal
@Composable
private fun OnboardingAddMealStep(
    currency: AppCurrency,
    mealPrices: Map<MealType, Double>
) {
    val detected = remember { MealType.detectCurrent() }
    val price = mealPrices[detected] ?: detected.defaultPrice

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.TouchApp,
                contentDescription = null,
                tint = BurgundyDark,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "3. Registro del Día",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = BurgundyDark,
            textAlign = TextAlign.Center
        )

        Text(
            text = "La app detecta automáticamente si es Desayuno, Almuerzo o Cena según la hora. Con un solo toque registrás tu plato.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, start = 8.dp, end = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mini Interactive Preview Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SoftCardBg),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CoralAccent.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "DETECCIÓN: ${detected.displayName.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = CoralAccent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = currency.format(price),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = BurgundyDark
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Fastfood,
                        contentDescription = null,
                        tint = CoralAccent,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Text(
                        text = "👆 Tocá el área en la pantalla principal para registrar",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

// STEP 4: How to add past meals
@Composable
private fun OnboardingPastMealsStep(
    currency: AppCurrency,
    mealPrices: Map<MealType, Double>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.CalendarToday,
                contentDescription = null,
                tint = BurgundyDark,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "4. Comidas de Otros Días",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = BurgundyDark,
            textAlign = TextAlign.Center
        )

        Text(
            text = "¿Olvidaste anotar una comida ayer o días pasados? Tocá el botón de calendario 'Otro día' para registrar consumos anteriores con fecha y notas.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, start = 8.dp, end = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SoftCardBg),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BurgundyDark,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.CalendarToday,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Botón 'Otro día' en la tarjeta central",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "O el icono de calendario en la barra superior.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MintPayment,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.Payments,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Botón de Abonos (💳)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Registrá abonos y pagos parciales para reducir tu deuda.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    ComidasTheme {
        OnboardingScreen(
            currentCurrency = AppCurrency.USD,
            availableCurrencies = AppCurrency.PREDEFINED_CURRENCIES,
            mealPrices = mapOf(
                MealType.BREAKFAST to 3.0,
                MealType.LUNCH to 5.0,
                MealType.DINNER to 4.0
            ),
            onCurrencySelected = {},
            onAddCustomCurrency = {},
            onUpdateMealPrice = { _, _ -> },
            onFinishOnboarding = {}
        )
    }
}
