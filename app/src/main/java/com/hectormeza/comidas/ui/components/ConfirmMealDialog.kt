package com.hectormeza.comidas.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.LunchDining
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConfirmMealDialog(
    initialMealType: MealType,
    mealPrices: Map<MealType, Double>,
    currency: AppCurrency,
    onDismiss: () -> Unit,
    onConfirm: (MealType, Int) -> Unit
) {
    var selectedMealType by remember { mutableStateOf(initialMealType) }
    var quantity by remember { mutableIntStateOf(1) }

    val unitPrice = mealPrices[selectedMealType] ?: selectedMealType.defaultPrice
    val totalPrice = unitPrice * quantity

    val currentDateStr = remember {
        val sdf = SimpleDateFormat("EEEE, dd 'de' MMMM", Locale.getDefault())
        sdf.format(Date()).replaceFirstChar { it.uppercase() }
    }

    val mealIcon: ImageVector = when (selectedMealType) {
        MealType.BREAKFAST -> Icons.Rounded.WbSunny
        MealType.LUNCH -> Icons.Rounded.LunchDining
        MealType.DINNER -> Icons.Rounded.Bedtime
    }

    val mealAccent: Color = when (selectedMealType) {
        MealType.BREAKFAST -> BreakfastAccent
        MealType.LUNCH -> LunchAccent
        MealType.DINNER -> DinnerAccent
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(mealAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = mealIcon,
                        contentDescription = null,
                        tint = mealAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "Registrar Comida",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = currentDateStr,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Meal Type selector chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MealType.entries.forEach { meal ->
                        val isSelected = meal == selectedMealType
                        val chipBg by animateColorAsState(
                            targetValue = if (isSelected) BurgundyDark else SoftCardBg,
                            label = "chipBg"
                        )
                        val chipTextColor = if (isSelected) Color.White else TextPrimary

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedMealType = meal },
                            shape = RoundedCornerShape(12.dp),
                            color = chipBg,
                            border = if (isSelected) null else BorderStroke(1.dp, CardBorder)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = meal.displayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = chipTextColor
                                )
                            }
                        }
                    }
                }

                // Quantity selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SoftCardBg)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "CANTIDAD",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            fontSize = 10.sp,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = if (quantity == 1) "1 porción" else "$quantity porciones",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledIconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            enabled = quantity > 1,
                            modifier = Modifier.size(34.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Remove,
                                contentDescription = "Disminuir",
                                tint = BurgundyDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = "$quantity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )

                        FilledIconButton(
                            onClick = { if (quantity < 20) quantity++ },
                            modifier = Modifier.size(34.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = BurgundyDark)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Aumentar",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Total price breakdown card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total a registrar:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        Text(
                            text = currency.format(totalPrice),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = BurgundyDark
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedMealType, quantity)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
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
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Confirmar ${selectedMealType.displayName} (${currency.format(totalPrice)})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Cancelar",
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun ConfirmMealDialogPreview() {
    ComidasTheme {
        ConfirmMealDialog(
            initialMealType = MealType.LUNCH,
            mealPrices = mapOf(
                MealType.BREAKFAST to 80.0,
                MealType.LUNCH to 130.0,
                MealType.DINNER to 100.0
            ),
            currency = AppCurrency.CORDOBA,
            onDismiss = {},
            onConfirm = { _, _ -> }
        )
    }
}
