package com.hectormeza.comidas.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hectormeza.comidas.model.AppCurrency
import com.hectormeza.comidas.ui.theme.BurgundyDark
import com.hectormeza.comidas.ui.theme.CardBorder
import com.hectormeza.comidas.ui.theme.ComidasTheme
import com.hectormeza.comidas.ui.theme.MintPayment
import com.hectormeza.comidas.ui.theme.SoftCardBg
import com.hectormeza.comidas.ui.theme.TextPrimary
import com.hectormeza.comidas.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddPaymentBottomSheet(
    sheetState: SheetState,
    currentDebt: Double,
    currency: AppCurrency,
    onDismiss: () -> Unit,
    onConfirmPayment: (amount: Double, note: String) -> Unit
) {
    var amountInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }
    val parsedAmount = amountInput.toDoubleOrNull() ?: 0.0
    val isValid = parsedAmount > 0.0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Registrar Abono",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Reduce el saldo de la deuda pendiente",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                TextButton(onClick = onDismiss) {
                    Text("Cancelar", color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Amount Input
            OutlinedTextField(
                value = amountInput,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("""^\d*\.?\d{0,2}$"""))) {
                        amountInput = input
                    }
                },
                label = { Text("Monto a abonar") },
                placeholder = { Text("0.00") },
                prefix = {
                    Text(
                        text = "${currency.symbol} ",
                        fontWeight = FontWeight.Black,
                        color = MintPayment
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = MintPayment,
                    unfocusedBorderColor = CardBorder,
                    focusedLabelColor = MintPayment,
                    unfocusedLabelColor = TextSecondary,
                    cursorColor = MintPayment
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick preset amount chips
            Text(
                text = "ACCESOS DIRECTOS DE ABONO:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(50.0, 100.0, 200.0, 500.0).forEach { preset ->
                    FilterChip(
                        selected = parsedAmount == preset,
                        onClick = { amountInput = preset.toInt().toString() },
                        label = { Text("+${currency.symbol}${preset.toInt()}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BurgundyDark,
                            selectedLabelColor = Color.White,
                            containerColor = SoftCardBg,
                            labelColor = TextPrimary
                        ),
                        border = if (parsedAmount == preset) null else BorderStroke(1.dp, CardBorder)
                    )
                }

                if (currentDebt > 0) {
                    FilterChip(
                        selected = parsedAmount == currentDebt,
                        onClick = { amountInput = String.format(Locale.US, "%.2f", currentDebt) },
                        label = { Text("Total (${currency.format(currentDebt)})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BurgundyDark,
                            selectedLabelColor = Color.White,
                            containerColor = SoftCardBg,
                            labelColor = TextPrimary
                        ),
                        border = if (parsedAmount == currentDebt) null else BorderStroke(1.dp, CardBorder)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Optional note input
            OutlinedTextField(
                value = noteInput,
                onValueChange = { noteInput = it },
                label = { Text("Nota u observación (opcional)") },
                placeholder = { Text("Ej. Transferencia, efectivo...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Description,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = BurgundyDark,
                    unfocusedBorderColor = CardBorder,
                    focusedLabelColor = BurgundyDark,
                    unfocusedLabelColor = TextSecondary,
                    cursorColor = BurgundyDark
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Confirm Button
            Button(
                onClick = {
                    if (isValid) {
                        onConfirmPayment(parsedAmount, noteInput)
                        onDismiss()
                    }
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurgundyDark,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isValid) "Confirmar Abono de ${currency.format(parsedAmount)}"
                        else "Ingresá un monto válido",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AddPaymentBottomSheetPreview() {
    ComidasTheme {
        AddPaymentBottomSheet(
            sheetState = rememberModalBottomSheetState(),
            currentDebt = 650.0,
            currency = AppCurrency.CORDOBA,
            onDismiss = {},
            onConfirmPayment = { _, _ -> }
        )
    }
}
