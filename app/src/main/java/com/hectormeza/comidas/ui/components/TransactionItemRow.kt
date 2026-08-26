package com.hectormeza.comidas.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.LunchDining
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.hectormeza.comidas.model.Transaction
import com.hectormeza.comidas.model.TransactionType
import com.hectormeza.comidas.ui.theme.BreakfastAccent
import com.hectormeza.comidas.ui.theme.BurgundyDark
import com.hectormeza.comidas.ui.theme.CardBorder
import com.hectormeza.comidas.ui.theme.ComidasTheme
import com.hectormeza.comidas.ui.theme.CoralAccent
import com.hectormeza.comidas.ui.theme.DinnerAccent
import com.hectormeza.comidas.ui.theme.LunchAccent
import com.hectormeza.comidas.ui.theme.MintPayment
import com.hectormeza.comidas.ui.theme.MintPaymentLight
import com.hectormeza.comidas.ui.theme.SoftCardBg
import com.hectormeza.comidas.ui.theme.TextPrimary
import com.hectormeza.comidas.ui.theme.TextSecondary

@Composable
fun TransactionItemRow(
    transaction: Transaction,
    currency: AppCurrency,
    modifier: Modifier = Modifier
) {
    val isMeal = transaction.type == TransactionType.MEAL
    val iconColor = when {
        !isMeal -> MintPayment
        transaction.mealType == MealType.BREAKFAST -> BreakfastAccent
        transaction.mealType == MealType.LUNCH -> LunchAccent
        transaction.mealType == MealType.DINNER -> DinnerAccent
        else -> CoralAccent
    }

    val icon: ImageVector = when {
        !isMeal -> Icons.Rounded.Payments
        transaction.mealType == MealType.BREAKFAST -> Icons.Rounded.WbSunny
        transaction.mealType == MealType.LUNCH -> Icons.Rounded.LunchDining
        transaction.mealType == MealType.DINNER -> Icons.Rounded.Bedtime
        else -> Icons.Rounded.Restaurant
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
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
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = transaction.displayTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${transaction.formattedDate} • ${transaction.formattedTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            // Amount Pill Badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isMeal) Color.White else MintPaymentLight,
                border = if (isMeal) BorderStroke(1.dp, CardBorder) else null
            ) {
                Text(
                    text = if (isMeal) "+${currency.format(transaction.amount)}"
                    else "-${currency.format(transaction.amount)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isMeal) BurgundyDark else MintPayment,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionItemRowPreview() {
    ComidasTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TransactionItemRow(
                transaction = Transaction(
                    id = "1",
                    type = TransactionType.MEAL,
                    amount = 130.0,
                    mealType = MealType.LUNCH
                ),
                currency = AppCurrency.CORDOBA
            )
            TransactionItemRow(
                transaction = Transaction(
                    id = "2",
                    type = TransactionType.PAYMENT,
                    amount = 200.0,
                    note = "Transferencia bancaria"
                ),
                currency = AppCurrency.CORDOBA
            )
        }
    }
}
