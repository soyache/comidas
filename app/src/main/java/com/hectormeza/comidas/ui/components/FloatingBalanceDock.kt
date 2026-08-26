package com.hectormeza.comidas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.LunchDining
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hectormeza.comidas.model.AppCurrency
import com.hectormeza.comidas.ui.theme.BreakfastAccent
import com.hectormeza.comidas.ui.theme.BurgundyContainer
import com.hectormeza.comidas.ui.theme.BurgundyDark
import com.hectormeza.comidas.ui.theme.DinnerAccent
import com.hectormeza.comidas.ui.theme.LunchAccent
import com.hectormeza.comidas.ui.theme.MintPayment

@Composable
fun FloatingBalanceDock(
    totalDebt: Double,
    totalMealsCount: Int,
    totalPaymentsAmount: Double,
    currency: AppCurrency,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(22.dp),
                spotColor = BurgundyDark.copy(alpha = 0.25f)
            )
            .clip(RoundedCornerShape(22.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = BurgundyContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Mini Thumbnails row (like the 3 garment thumbnails in "IN THE BAG")
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MiniMealThumbnail(
                    icon = Icons.Rounded.WbSunny,
                    accentColor = BreakfastAccent
                )
                MiniMealThumbnail(
                    icon = Icons.Rounded.LunchDining,
                    accentColor = LunchAccent
                )
                MiniMealThumbnail(
                    icon = Icons.Rounded.Bedtime,
                    accentColor = DinnerAccent
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Center details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "TOTAL ADEUDADO",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.8f),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "$totalMealsCount comidas • Abonos: ${currency.format(totalPaymentsAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.65f),
                    maxLines = 1
                )
            }

            // Price & arrow
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = currency.format(totalDebt),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun MiniMealThumbnail(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(18.dp)
        )
    }
}
