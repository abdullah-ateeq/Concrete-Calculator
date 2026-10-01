package com.example.concretecalculator.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val blockProgress = remember { Animatable(0f) }
    val linesProgress = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Step 1: 3D Slab emerges (0 to 450ms)
        launch {
            blockProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            )
        }
        // Step 2: Dimension lines and volume badge appear (300 to 800ms)
        delay(250)
        launch {
            linesProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 450, easing = LinearOutSlowInEasing)
            )
        }
        // Step 3: Title and typography fade in (500 to 1100ms)
        delay(250)
        launch {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400)
            )
        }

        // Total duration ~1.3-1.4 seconds, then proceed to calculator
        delay(750)
        onSplashFinished()
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outline
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Isometric 3D Concrete Slab with Dimension Lines
            Canvas(modifier = Modifier.size(160.dp)) {
                val p = blockProgress.value
                val lp = linesProgress.value

                val centerX = size.width / 2f
                val centerY = size.height / 2f + 10.dp.toPx()

                val slabW = 60.dp.toPx() * p
                val slabH = 30.dp.toPx() * p
                val slabD = 22.dp.toPx() * p

                // Top Face (Rhombus)
                val topPath = Path().apply {
                    moveTo(centerX, centerY - slabH)
                    lineTo(centerX + slabW, centerY - slabH + slabD * 0.5f)
                    lineTo(centerX, centerY - slabH + slabD)
                    lineTo(centerX - slabW, centerY - slabH + slabD * 0.5f)
                    close()
                }
                drawPath(topPath, color = surfaceColor)
                drawPath(topPath, color = outlineColor, style = Stroke(width = 2.dp.toPx()))

                // Left/Front Face
                val leftPath = Path().apply {
                    moveTo(centerX - slabW, centerY - slabH + slabD * 0.5f)
                    lineTo(centerX, centerY - slabH + slabD)
                    lineTo(centerX, centerY + slabD)
                    lineTo(centerX - slabW, centerY + slabD * 0.5f)
                    close()
                }
                drawPath(leftPath, color = surfaceColor.copy(alpha = 0.85f))
                drawPath(leftPath, color = outlineColor, style = Stroke(width = 2.dp.toPx()))

                // Right/Front Face
                val rightPath = Path().apply {
                    moveTo(centerX, centerY - slabH + slabD)
                    lineTo(centerX + slabW, centerY - slabH + slabD * 0.5f)
                    lineTo(centerX + slabW, centerY + slabD * 0.5f)
                    lineTo(centerX, centerY + slabD)
                    close()
                }
                drawPath(rightPath, color = surfaceColor.copy(alpha = 0.7f))
                drawPath(rightPath, color = outlineColor, style = Stroke(width = 2.dp.toPx()))

                // Technical Dimension Lines & Tick Marks (animated in)
                if (lp > 0f) {
                    val lineColor = primaryColor.copy(alpha = lp)
                    val strokeW = 1.5.dp.toPx()

                    // Depth indicator on right edge
                    val rightX = centerX + slabW + 12.dp.toPx()
                    val rightTopY = centerY - slabH + slabD * 0.5f
                    val rightBottomY = centerY + slabD * 0.5f

                    drawLine(
                        color = lineColor,
                        start = Offset(rightX, rightTopY),
                        end = Offset(rightX, rightTopY + (rightBottomY - rightTopY) * lp),
                        strokeWidth = strokeW
                    )
                    // Ticks
                    drawLine(
                        color = lineColor,
                        start = Offset(rightX - 4.dp.toPx(), rightTopY),
                        end = Offset(rightX + 4.dp.toPx(), rightTopY),
                        strokeWidth = strokeW
                    )
                    drawLine(
                        color = lineColor,
                        start = Offset(rightX - 4.dp.toPx(), rightBottomY),
                        end = Offset(rightX + 4.dp.toPx(), rightBottomY),
                        strokeWidth = strokeW
                    )

                    // Width indicator on bottom-left
                    val leftBottomStartX = centerX - slabW - 8.dp.toPx()
                    val leftBottomStartY = centerY + slabD * 0.5f + 10.dp.toPx()
                    val leftBottomEndX = centerX - 8.dp.toPx()
                    val leftBottomEndY = centerY + slabD + 10.dp.toPx()

                    drawLine(
                        color = lineColor,
                        start = Offset(leftBottomStartX, leftBottomStartY),
                        end = Offset(
                            leftBottomStartX + (leftBottomEndX - leftBottomStartX) * lp,
                            leftBottomStartY + (leftBottomEndY - leftBottomStartY) * lp
                        ),
                        strokeWidth = strokeW
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Title
            Text(
                text = "CONCRETE CALCULATOR",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = textAlpha.value)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Professional Volume & Material Estimation",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = textAlpha.value)
            )
        }
    }
}
