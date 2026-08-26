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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.LunchDining
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hectormeza.comidas.model.AppCurrency
import com.hectormeza.comidas.model.MealType
import com.hectormeza.comidas.ui.theme.BreakfastAccent
import com.hectormeza.comidas.ui.theme.BurgundyDark
import com.hectormeza.comidas.ui.theme.CoralAccent
import com.hectormeza.comidas.ui.theme.DinnerAccent
import com.hectormeza.comidas.ui.theme.LunchAccent
import com.hectormeza.comidas.ui.theme.SoftCardBg
import com.hectormeza.comidas.ui.theme.TextPrimary
import com.hectormeza.comidas.ui.theme.TextSecondary

@Composable
fun MealGridCard(
    mealType: MealType,
    price: Double,
    currency: AppCurrency,
    isCurrentTimeSuggested: Boolean,
    onAddMeal: (MealType) -> Unit,
    modifier: Modifier = Modifier
) {
    val icon: ImageVector = when (mealType) {
        MealType.BREAKFAST -> Icons.Rounded.WbSunny
        MealType.LUNCH -> Icons.Rounded.LunchDining
        MealType.DINNER -> Icons.Rounded.Bedtime
    }

    val accentColor: Color = when (mealType) {
        MealType.BREAKFAST -> BreakfastAccent
        MealType.LUNCH -> LunchAccent
        MealType.DINNER -> DinnerAccent
    }

    Column(modifier = modifier) {
        // Main Visual Card Frame (inspired by the reference clothing cards)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(24.dp))
                .clickable { onAddMeal(mealType) },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SoftCardBg),
            border = if (isCurrentTimeSuggested) BorderStroke(1.5.dp, BurgundyDark) else BorderStroke(1.dp, Color(0xFFEBE7EA))
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                // Top Right Price Badge (like "185K" in reference)
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd),
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = currency.format(price),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = BurgundyDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Center Icon Illustration Area
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = mealType.displayName,
                        tint = accentColor,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Bottom Left Schedule Badge (like "30", "M" size pills in reference)
                Surface(
                    modifier = Modifier.align(Alignment.BottomStart),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = mealType.timeRangeDescription.split(" - ")[0],
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Quick Add "+" FAB inside card bottom right
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(if (isCurrentTimeSuggested) BurgundyDark else Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Agregar",
                        tint = if (isCurrentTimeSuggested) Color.White else BurgundyDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Card Labels (Exact typographic hierarchy from reference UI)
        Text(
            text = "TIEMPO DE COMIDA",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            fontSize = 10.sp,
            letterSpacing = 0.8.sp
        )

        Text(
            text = mealType.displayName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        // Status pill in red/coral like "VERY GOOD", "LIKE NEW" in reference
        Text(
            text = if (isCurrentTimeSuggested) "DETECTADO AHORA" else "REGISTRAR",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = if (isCurrentTimeSuggested) CoralAccent else TextSecondary,
            fontSize = 11.sp,
            letterSpacing = 0.5.sp
        )
    }
}
