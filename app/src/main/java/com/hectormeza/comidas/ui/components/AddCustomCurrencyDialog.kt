package com.hectormeza.comidas.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.hectormeza.comidas.model.AppCurrency
import com.hectormeza.comidas.ui.theme.BurgundyDark
import com.hectormeza.comidas.ui.theme.CardBorder
import com.hectormeza.comidas.ui.theme.SoftCardBg
import com.hectormeza.comidas.ui.theme.TextPrimary
import com.hectormeza.comidas.ui.theme.TextSecondary

@Composable
fun AddCustomCurrencyDialog(
    onDismiss: () -> Unit,
    onConfirm: (AppCurrency) -> Unit
) {
    var symbolInput by remember { mutableStateOf("") }
    var codeInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }

    val isValid = symbolInput.isNotBlank() && codeInput.isNotBlank()

    val textFieldColors = OutlinedTextFieldDefaults.colors(
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

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Nueva Moneda Personalizada",
                fontWeight = FontWeight.Bold,
                color = BurgundyDark,
                style = MaterialTheme.typography.titleMedium
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
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = codeInput,
                    onValueChange = { codeInput = it.uppercase().take(5) },
                    label = { Text("Código / Abreviatura (ej. GTQ, BOB)") },
                    placeholder = { Text("ej. GTQ") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Nombre (opcional)") },
                    placeholder = { Text("ej. Quetzal guatemalteco") },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (isValid) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
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
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurgundyDark,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Guardar y Usar",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Cancelar",
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}
