package com.hectormeza.comidas.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Restaurant
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
import com.hectormeza.comidas.ui.theme.BurgundyDark
import com.hectormeza.comidas.ui.theme.CardBorder
import com.hectormeza.comidas.ui.theme.ComidasTheme
import com.hectormeza.comidas.ui.theme.CoralAccent
import com.hectormeza.comidas.ui.theme.CoralLight
import com.hectormeza.comidas.ui.theme.MintPayment
import com.hectormeza.comidas.ui.theme.MintPaymentLight
import com.hectormeza.comidas.ui.theme.SoftCardBg
import com.hectormeza.comidas.ui.theme.TextPrimary
import com.hectormeza.comidas.ui.theme.TextSecondary

@Composable
fun DebtSummaryCard(
    totalDebt: Double,
    totalMealsCount: Int,
    totalPaymentsAmount: Double,
    currency: AppCurrency,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = SoftCardBg),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AccountBalanceWallet,
                            contentDescription = "Saldo",
                            tint = BurgundyDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ESTADO DE CUENTA",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (totalDebt > 0) "Saldo pendiente" else "Sin deuda acumulada",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (totalDebt > 0) CoralAccent else MintPayment,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = currency.format(totalDebt),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 38.sp
                ),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sub-metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricPill(
                    icon = Icons.Rounded.Restaurant,
                    label = "$totalMealsCount comidas",
                    containerColor = Color.White,
                    contentColor = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                MetricPill(
                    icon = Icons.Rounded.Payments,
                    label = "Abonos: ${currency.format(totalPaymentsAmount)}",
                    containerColor = MintPaymentLight,
                    contentColor = MintPayment,
                    modifier = Modifier.weight(1.3f)
                )
            }
        }
    }
}

@Composable
private fun MetricPill(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DebtSummaryCardPreview() {
    ComidasTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            DebtSummaryCard(
                totalDebt = 1450.0,
                totalMealsCount = 14,
                totalPaymentsAmount = 500.0,
                currency = AppCurrency.CORDOBA
            )
        }
    }
}
