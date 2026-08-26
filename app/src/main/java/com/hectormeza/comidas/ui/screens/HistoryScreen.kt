package com.hectormeza.comidas.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hectormeza.comidas.model.AppCurrency
import com.hectormeza.comidas.model.MealType
import com.hectormeza.comidas.model.Transaction
import com.hectormeza.comidas.model.TransactionType
import com.hectormeza.comidas.ui.components.TransactionItemRow
import com.hectormeza.comidas.ui.theme.BurgundyDark
import com.hectormeza.comidas.ui.theme.CardBorder
import com.hectormeza.comidas.ui.theme.ComidasTheme
import com.hectormeza.comidas.ui.theme.CoralAccent
import com.hectormeza.comidas.ui.theme.MintPayment
import com.hectormeza.comidas.ui.theme.SoftCardBg
import com.hectormeza.comidas.ui.theme.TextPrimary
import com.hectormeza.comidas.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class HistoryTypeFilter(val label: String) {
    ALL("Todos"),
    MEALS("Comidas"),
    PAYMENTS("Abonos")
}

enum class DateRangePreset(val label: String) {
    ALL_TIME("Siempre"),
    TODAY("Hoy"),
    THIS_WEEK("Esta semana"),
    THIS_MONTH("Este mes"),
    CUSTOM("Rango de fechas...")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    transactions: List<Transaction>,
    currency: AppCurrency,
    modifier: Modifier = Modifier
) {
    var selectedTypeFilter by remember { mutableStateOf(HistoryTypeFilter.ALL) }
    var selectedDatePreset by remember { mutableStateOf(DateRangePreset.ALL_TIME) }
    var customStartDate by remember { mutableStateOf<Long?>(null) }
    var customEndDate by remember { mutableStateOf<Long?>(null) }
    var showDateRangeDialog by remember { mutableStateOf(false) }

    val dateRangePickerState = rememberDateRangePickerState()

    // Calculate effective date bounds
    val (startBound, endBound) = remember(selectedDatePreset, customStartDate, customEndDate) {
        val now = System.currentTimeMillis()
        when (selectedDatePreset) {
            DateRangePreset.ALL_TIME -> null to null
            DateRangePreset.TODAY -> getStartOfDay(now) to getEndOfDay(now)
            DateRangePreset.THIS_WEEK -> getStartOfWeek(now) to getEndOfDay(now)
            DateRangePreset.THIS_MONTH -> getStartOfMonth(now) to getEndOfDay(now)
            DateRangePreset.CUSTOM -> {
                val start = customStartDate?.let { getStartOfDay(it) }
                val end = customEndDate?.let { getEndOfDay(it) }
                start to end
            }
        }
    }

    // Filter transactions
    val filteredTransactions = remember(transactions, selectedTypeFilter, startBound, endBound) {
        transactions.filter { tx ->
            val matchesType = when (selectedTypeFilter) {
                HistoryTypeFilter.ALL -> true
                HistoryTypeFilter.MEALS -> tx.type == TransactionType.MEAL
                HistoryTypeFilter.PAYMENTS -> tx.type == TransactionType.PAYMENT
            }

            val matchesDate = when {
                startBound != null && endBound != null -> tx.timestamp in startBound..endBound
                startBound != null -> tx.timestamp >= startBound
                endBound != null -> tx.timestamp <= endBound
                else -> true
            }

            matchesType && matchesDate
        }
    }

    // Summary calculations for the filtered set
    val filteredMealsCount = filteredTransactions.filter { it.type == TransactionType.MEAL }.sumOf { it.quantity }
    val filteredMealsSum = filteredTransactions.filter { it.type == TransactionType.MEAL }.sumOf { it.amount }
    val filteredPaymentsSum = filteredTransactions.filter { it.type == TransactionType.PAYMENT }.sumOf { it.amount }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "HISTORIAL",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    ),
                    color = BurgundyDark
                )
                Text(
                    text = "REGISTRO Y BALANCE DE ACTIVIDAD",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    letterSpacing = 0.8.sp
                )
            }
        }

        // Filter 1: Type selector chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HistoryTypeFilter.entries.forEach { filter ->
                    val isSelected = selectedTypeFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTypeFilter = filter },
                        label = { Text(filter.label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BurgundyDark,
                            selectedLabelColor = Color.White,
                            containerColor = SoftCardBg,
                            labelColor = TextPrimary
                        ),
                        border = if (isSelected) null else BorderStroke(1.dp, CardBorder)
                    )
                }
            }
        }

        // Filter 2: Date presets row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DateRangePreset.entries.forEach { preset ->
                    val isSelected = selectedDatePreset == preset
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (preset == DateRangePreset.CUSTOM) {
                                showDateRangeDialog = true
                            } else {
                                selectedDatePreset = preset
                            }
                        },
                        leadingIcon = if (preset == DateRangePreset.CUSTOM) {
                            {
                                Icon(
                                    imageVector = Icons.Rounded.DateRange,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isSelected) Color.White else BurgundyDark
                                )
                            }
                        } else null,
                        label = { Text(preset.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BurgundyDark,
                            selectedLabelColor = Color.White,
                            containerColor = SoftCardBg,
                            labelColor = TextPrimary
                        ),
                        border = if (isSelected) null else BorderStroke(1.dp, CardBorder)
                    )
                }
            }
        }

        // Custom Date Range Active Badge
        if (selectedDatePreset == DateRangePreset.CUSTOM && (customStartDate != null || customEndDate != null)) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SoftCardBg,
                    border = BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.CalendarMonth,
                                contentDescription = null,
                                tint = BurgundyDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Rango: ${formatRangeText(customStartDate, customEndDate)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = BurgundyDark
                            )
                        }

                        IconButton(
                            onClick = {
                                selectedDatePreset = DateRangePreset.ALL_TIME
                                customStartDate = null
                                customEndDate = null
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Limpiar rango",
                                tint = BurgundyDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Summary metric card for filtered items
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SoftCardBg),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TOTAL COMIDAS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "$filteredMealsCount (${currency.format(filteredMealsSum)})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(28.dp)
                            .width(1.dp)
                            .padding(vertical = 2.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TOTAL ABONOS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = currency.format(filteredPaymentsSum),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MintPayment
                        )
                    }
                }
            }
        }

        // Items or empty state
        if (filteredTransactions.isEmpty()) {
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
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FilterList,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No hay registros en este rango",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Probá cambiando las fechas o el filtro de comidas.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(filteredTransactions, key = { it.id }) { transaction ->
                TransactionItemRow(
                    transaction = transaction,
                    currency = currency
                )
            }
        }

        // Bottom spacer for floating bottom bar
        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }

    // Material 3 Date Range Picker Dialog
    if (showDateRangeDialog) {
        DatePickerDialog(
            onDismissRequest = { showDateRangeDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val start = dateRangePickerState.selectedStartDateMillis
                        val end = dateRangePickerState.selectedEndDateMillis ?: start
                        if (start != null) {
                            customStartDate = start
                            customEndDate = end
                            selectedDatePreset = DateRangePreset.CUSTOM
                        }
                        showDateRangeDialog = false
                    }
                ) {
                    Text("Aplicar Rango", fontWeight = FontWeight.Bold, color = BurgundyDark)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangeDialog = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// Date calculation helpers
private fun getStartOfDay(timestamp: Long): Long {
    val cal = Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

private fun getEndOfDay(timestamp: Long): Long {
    val cal = Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
    return cal.timeInMillis
}

private fun getStartOfWeek(timestamp: Long): Long {
    val cal = Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

private fun getStartOfMonth(timestamp: Long): Long {
    val cal = Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

private fun formatRangeText(start: Long?, end: Long?): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return when {
        start != null && end != null -> "${sdf.format(Date(start))} - ${sdf.format(Date(end))}"
        start != null -> "Desde ${sdf.format(Date(start))}"
        end != null -> "Hasta ${sdf.format(Date(end))}"
        else -> "Todas las fechas"
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    ComidasTheme {
        HistoryScreen(
            transactions = listOf(
                Transaction(
                    id = "1",
                    type = TransactionType.MEAL,
                    amount = 80.0,
                    mealType = MealType.BREAKFAST
                ),
                Transaction(
                    id = "2",
                    type = TransactionType.MEAL,
                    amount = 130.0,
                    mealType = MealType.LUNCH
                ),
                Transaction(
                    id = "3",
                    type = TransactionType.PAYMENT,
                    amount = 150.0,
                    note = "Pago quincenal"
                )
            ),
            currency = AppCurrency.CORDOBA
        )
    }
}
