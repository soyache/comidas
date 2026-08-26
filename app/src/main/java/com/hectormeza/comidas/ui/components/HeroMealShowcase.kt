package com.hectormeza.comidas.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.LunchDining
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import com.hectormeza.comidas.ui.theme.CoralAccent
import com.hectormeza.comidas.ui.theme.DinnerAccent
import com.hectormeza.comidas.ui.theme.LunchAccent
import com.hectormeza.comidas.ui.theme.SoftCardBg
import com.hectormeza.comidas.ui.theme.TextSecondary

@Composable
fun HeroMealShowcase(
    currentMealType: MealType,
    price: Double,
    currency: AppCurrency,
    onShowcaseClick: () -> Unit,
    onPastDateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mealIcon: ImageVector = when (currentMealType) {
        MealType.BREAKFAST -> Icons.Rounded.WbSunny
        MealType.LUNCH -> Icons.Rounded.LunchDining
        MealType.DINNER -> Icons.Rounded.Bedtime
    }

    val mealAccent: Color = when (currentMealType) {
        MealType.BREAKFAST -> BreakfastAccent
        MealType.LUNCH -> LunchAccent
        MealType.DINNER -> DinnerAccent
    }

    // Main Showcase Stage (Hero presentation like the clothes hanger showcase in reference)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(310.dp)
            .clip(RoundedCornerShape(32.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onShowcaseClick() },
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = SoftCardBg),
            border = BorderStroke(1.dp, CardBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Subtle aesthetic background gradient glow
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                mealAccent.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Top Left: Clickable Past Date Icon Button
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clip(CircleShape)
                    .clickable { onPastDateClick() },
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(1.dp, CardBorder),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CalendarMonth,
                        contentDescription = "Registrar otro día",
                        tint = BurgundyDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Otro día",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = BurgundyDark,
                        fontSize = 11.sp
                    )
                }
            }

            // Top Right: Hanging Price Tag (directly inspired by the price tag in the reference)
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(18.dp),
                        spotColor = BurgundyDark.copy(alpha = 0.18f)
                    ),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currency.code,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = currency.format(price, includeSymbol = false),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp
                        ),
                        color = BurgundyDark
                    )

                    Box(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .height(1.dp)
                            .width(36.dp)
                            .background(CardBorder)
                    )

                    Text(
                        text = currentMealType.timeRangeDescription.split(" - ")[0],
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        fontSize = 9.sp
                    )

                    Text(
                        text = "DETECTADO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = CoralAccent,
                        fontSize = 9.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Center Stage Graphic: Hero Meal Icon in styled white container
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            spotColor = mealAccent.copy(alpha = 0.3f)
                        )
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = mealIcon,
                        contentDescription = currentMealType.displayName,
                        tint = mealAccent,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            // Bottom Center Action Indicator (like "DRAG DOWN TO BAG IT" in reference)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.85f))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.TouchApp,
                    contentDescription = null,
                    tint = BurgundyDark,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "TOCÁ EL ÁREA PARA REGISTRAR",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = BurgundyDark,
                    fontSize = 10.sp,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeroMealShowcasePreview() {
    ComidasTheme {
        HeroMealShowcase(
            currentMealType = MealType.LUNCH,
            price = 130.0,
            currency = AppCurrency.CORDOBA,
            onShowcaseClick = {},
            onPastDateClick = {}
        )
    }
}
