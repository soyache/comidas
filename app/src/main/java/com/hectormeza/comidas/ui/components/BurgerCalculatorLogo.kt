package com.hectormeza.comidas.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Logo Colors
val BunColor = Color(0xFFE89B5F)
val SesameColor = Color(0xFFFDE68A)
val LettuceColor = Color(0xFF10B981)
val CheeseColor = Color(0xFFF59E0B)
val PattyColor = Color(0xFF6A381F)
val KeyColor = Color.White

@Composable
fun BurgerCalculatorLogo(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    animateEntrance: Boolean = false
) {
    val topBunOffset = remember { Animatable(if (animateEntrance) -80f else 0f) }
    val lettuceOffset = remember { Animatable(if (animateEntrance) -30f else 0f) }
    val cheeseOffset = remember { Animatable(if (animateEntrance) -20f else 0f) }
    val pattyScale = remember { Animatable(if (animateEntrance) 0.3f else 1f) }
    val bottomBunOffset = remember { Animatable(if (animateEntrance) 60f else 0f) }
    val keysAlpha = remember { Animatable(if (animateEntrance) 0f else 1f) }

    // Subtle gentle idle breathing animation for life
    val infiniteTransition = rememberInfiniteTransition(label = "idleBreath")
    val idleBounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idleBounce"
    )

    if (animateEntrance) {
        LaunchedEffect(Unit) {
            // 1. Bottom Bun rises
            launch {
                bottomBunOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                )
            }

            // 2. Patty pops in
            delay(100)
            launch {
                pattyScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
                )
                keysAlpha.animateTo(1f, animationSpec = tween(300))
            }

            // 3. Cheese & Lettuce slide into place
            delay(180)
            launch {
                cheeseOffset.animateTo(0f, animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy))
            }
            launch {
                lettuceOffset.animateTo(0f, animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy))
            }

            // 4. Top Bun drops with squash & stretch bounce
            delay(260)
            launch {
                topBunOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                )
            }
        }
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = this.size.width
            val scale = canvasWidth / 108f

            val effectiveIdle = if (!animateEntrance || topBunOffset.value == 0f) idleBounce else 0f

            // 1. Bottom Bun
            drawBottomBun(
                scale = scale,
                yOffset = bottomBunOffset.value * scale
            )

            // 2. Patty & Calculator Keys
            drawPattyWithKeys(
                scale = scale,
                scaleFactor = pattyScale.value,
                keysAlpha = keysAlpha.value
            )

            // 3. Melted Cheese
            drawCheese(
                scale = scale,
                yOffset = cheeseOffset.value * scale
            )

            // 4. Lettuce Layer
            drawLettuce(
                scale = scale,
                yOffset = lettuceOffset.value * scale
            )

            // 5. Top Bun (Pure golden bun with sesame seeds)
            drawTopBun(
                scale = scale,
                yOffset = (topBunOffset.value - effectiveIdle) * scale
            )
        }
    }
}

private fun DrawScope.drawTopBun(scale: Float, yOffset: Float) {
    val bunPath = Path().apply {
        moveTo(34f * scale, (44f * scale) + yOffset)
        cubicTo(
            34f * scale, (30f * scale) + yOffset,
            42f * scale, (24f * scale) + yOffset,
            54f * scale, (24f * scale) + yOffset
        )
        cubicTo(
            66f * scale, (24f * scale) + yOffset,
            74f * scale, (30f * scale) + yOffset,
            74f * scale, (44f * scale) + yOffset
        )
        close()
    }
    drawPath(bunPath, color = BunColor)

    // Sesame seeds on pure golden bun
    val sesameColor = SesameColor
    drawRoundRect(
        color = sesameColor,
        topLeft = Offset(40f * scale, (34f * scale) + yOffset),
        size = Size(3f * scale, 2f * scale),
        cornerRadius = CornerRadius(1f * scale)
    )
    drawRoundRect(
        color = sesameColor,
        topLeft = Offset(48f * scale, (28f * scale) + yOffset),
        size = Size(3f * scale, 2f * scale),
        cornerRadius = CornerRadius(1f * scale)
    )
    drawRoundRect(
        color = sesameColor,
        topLeft = Offset(57f * scale, (28f * scale) + yOffset),
        size = Size(3f * scale, 2f * scale),
        cornerRadius = CornerRadius(1f * scale)
    )
    drawRoundRect(
        color = sesameColor,
        topLeft = Offset(65f * scale, (34f * scale) + yOffset),
        size = Size(3f * scale, 2f * scale),
        cornerRadius = CornerRadius(1f * scale)
    )
    drawRoundRect(
        color = sesameColor,
        topLeft = Offset(52f * scale, (35f * scale) + yOffset),
        size = Size(3f * scale, 2f * scale),
        cornerRadius = CornerRadius(1f * scale)
    )
}

private fun DrawScope.drawLettuce(scale: Float, yOffset: Float) {
    val lettucePath = Path().apply {
        moveTo(31f * scale, (45f * scale) + yOffset)
        cubicTo(34f * scale, (47.5f * scale) + yOffset, 38f * scale, (45f * scale) + yOffset, 42f * scale, (47.5f * scale) + yOffset)
        cubicTo(46f * scale, (45f * scale) + yOffset, 50f * scale, (47.5f * scale) + yOffset, 54f * scale, (45f * scale) + yOffset)
        cubicTo(58f * scale, (47.5f * scale) + yOffset, 62f * scale, (45f * scale) + yOffset, 66f * scale, (47.5f * scale) + yOffset)
        cubicTo(70f * scale, (45f * scale) + yOffset, 74f * scale, (47.5f * scale) + yOffset, 77f * scale, (45f * scale) + yOffset)
        cubicTo(78f * scale, (48f * scale) + yOffset, 75f * scale, (48.5f * scale) + yOffset, 73f * scale, (48.5f * scale) + yOffset)
        cubicTo(69f * scale, (48.5f * scale) + yOffset, 65f * scale, (46.5f * scale) + yOffset, 61f * scale, (48.5f * scale) + yOffset)
        cubicTo(57f * scale, (46.5f * scale) + yOffset, 53f * scale, (48.5f * scale) + yOffset, 49f * scale, (46.5f * scale) + yOffset)
        cubicTo(45f * scale, (48.5f * scale) + yOffset, 41f * scale, (46.5f * scale) + yOffset, 37f * scale, (48.5f * scale) + yOffset)
        cubicTo(33f * scale, (46.5f * scale) + yOffset, 30f * scale, (48f * scale) + yOffset, 31f * scale, (45f * scale) + yOffset)
        close()
    }
    drawPath(lettucePath, color = LettuceColor)
}

private fun DrawScope.drawCheese(scale: Float, yOffset: Float) {
    val cheesePath = Path().apply {
        moveTo(33f * scale, (47.5f * scale) + yOffset)
        lineTo(75f * scale, (47.5f * scale) + yOffset)
        lineTo(68f * scale, (55f * scale) + yOffset)
        lineTo(61f * scale, (47.5f * scale) + yOffset)
        lineTo(51f * scale, (47.5f * scale) + yOffset)
        lineTo(45f * scale, (54f * scale) + yOffset)
        lineTo(40f * scale, (47.5f * scale) + yOffset)
        close()
    }
    drawPath(cheesePath, color = CheeseColor)
}

private fun DrawScope.drawPattyWithKeys(scale: Float, scaleFactor: Float, keysAlpha: Float) {
    val pattyWidth = 44f * scale * scaleFactor
    val pattyHeight = 11f * scale * scaleFactor
    val pattyLeft = (54f * scale) - (pattyWidth / 2f)
    val pattyTop = (55f * scale) - (pattyHeight / 2f)

    drawRoundRect(
        color = PattyColor,
        topLeft = Offset(pattyLeft, pattyTop),
        size = Size(pattyWidth, pattyHeight),
        cornerRadius = CornerRadius(4.5f * scale)
    )

    if (keysAlpha > 0f && scaleFactor > 0.8f) {
        val keyColor = KeyColor.copy(alpha = keysAlpha)

        // Key 1: [+]
        drawRoundRect(
            color = keyColor,
            topLeft = Offset(38.5f * scale, 53.5f * scale),
            size = Size(3.5f * scale, 1.2f * scale),
            cornerRadius = CornerRadius(0.5f * scale)
        )
        drawRoundRect(
            color = keyColor,
            topLeft = Offset(39.65f * scale, 52.35f * scale),
            size = Size(1.2f * scale, 3.5f * scale),
            cornerRadius = CornerRadius(0.5f * scale)
        )

        // Key 2: [-]
        drawRoundRect(
            color = keyColor,
            topLeft = Offset(47.5f * scale, 54f * scale),
            size = Size(4f * scale, 1.2f * scale),
            cornerRadius = CornerRadius(0.5f * scale)
        )

        // Key 3: [×]
        drawLine(
            color = keyColor,
            start = Offset(57f * scale, 53f * scale),
            end = Offset(60f * scale, 56f * scale),
            strokeWidth = 1.2f * scale
        )
        drawLine(
            color = keyColor,
            start = Offset(60f * scale, 53f * scale),
            end = Offset(57f * scale, 56f * scale),
            strokeWidth = 1.2f * scale
        )

        // Key 4: [=]
        drawRoundRect(
            color = keyColor,
            topLeft = Offset(65.5f * scale, 53.2f * scale),
            size = Size(4f * scale, 1.1f * scale),
            cornerRadius = CornerRadius(0.5f * scale)
        )
        drawRoundRect(
            color = keyColor,
            topLeft = Offset(65.5f * scale, 55.2f * scale),
            size = Size(4f * scale, 1.1f * scale),
            cornerRadius = CornerRadius(0.5f * scale)
        )
    }
}

private fun DrawScope.drawBottomBun(scale: Float, yOffset: Float) {
    val bottomBunPath = Path().apply {
        moveTo(35f * scale, (62f * scale) + yOffset)
        lineTo(73f * scale, (62f * scale) + yOffset)
        cubicTo(
            77f * scale, (62f * scale) + yOffset,
            77f * scale, (71f * scale) + yOffset,
            71f * scale, (71f * scale) + yOffset
        )
        lineTo(37f * scale, (71f * scale) + yOffset)
        cubicTo(
            31f * scale, (71f * scale) + yOffset,
            31f * scale, (62f * scale) + yOffset,
            35f * scale, (62f * scale) + yOffset
        )
        close()
    }
    drawPath(bottomBunPath, color = BunColor)
}

@Preview(showBackground = true)
@Composable
fun BurgerCalculatorLogoPreview() {
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color(0xFF240A18)),
        contentAlignment = Alignment.Center
    ) {
        BurgerCalculatorLogo(size = 140.dp)
    }
}
