package com.example.teacare.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.teacare.ui.theme.TeaGreenLight
import com.example.teacare.ui.theme.TeaGreenPrimary

@Composable
fun TeaLeafLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    tint: Color = TeaGreenPrimary,
    glow: Boolean = true
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (glow) {
            Box(
                modifier = Modifier
                    .size(size * 0.9f)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(TeaGreenLight.copy(alpha = 0.6f))
            )
        }
        Canvas(modifier = Modifier.size(size * 0.7f)) {
            val width = this.size.width
            val height = this.size.height

            // Cành chính
            val stemPath = Path().apply {
                moveTo(width * 0.5f, height * 0.95f)
                cubicTo(
                    width * 0.48f, height * 0.7f,
                    width * 0.5f, height * 0.5f,
                    width * 0.5f, height * 0.15f
                )
            }
            drawPath(
                path = stemPath,
                color = tint,
                style = Stroke(width = width * 0.05f, cap = StrokeCap.Round)
            )

            // Lá búp giữa (Búp tôm non)
            val centerLeafPath = Path().apply {
                moveTo(width * 0.5f, height * 0.12f)
                cubicTo(
                    width * 0.62f, height * 0.25f,
                    width * 0.58f, height * 0.45f,
                    width * 0.5f, height * 0.5f
                )
                cubicTo(
                    width * 0.42f, height * 0.45f,
                    width * 0.38f, height * 0.25f,
                    width * 0.5f, height * 0.12f
                )
            }
            drawPath(path = centerLeafPath, color = tint)

            // Lá bên phải (Lá non bánh tẻ 1)
            val rightLeafPath = Path().apply {
                moveTo(width * 0.5f, height * 0.45f)
                cubicTo(
                    width * 0.85f, height * 0.35f,
                    width * 0.95f, height * 0.55f,
                    width * 0.52f, height * 0.68f
                )
                cubicTo(
                    width * 0.68f, height * 0.65f,
                    width * 0.65f, height * 0.5f,
                    width * 0.5f, height * 0.45f
                )
            }
            drawPath(path = rightLeafPath, color = tint.copy(alpha = 0.9f))

            // Lá bên trái (Lá non bánh tẻ 2)
            val leftLeafPath = Path().apply {
                moveTo(width * 0.49f, height * 0.55f)
                cubicTo(
                    width * 0.12f, height * 0.45f,
                    width * 0.08f, height * 0.70f,
                    width * 0.48f, height * 0.78f
                )
                cubicTo(
                    width * 0.32f, height * 0.75f,
                    width * 0.35f, height * 0.60f,
                    width * 0.49f, height * 0.55f
                )
            }
            drawPath(path = leftLeafPath, color = tint.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun ScanningLaserEffect(
    isScanning: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isScanning) return

    val infiniteTransition = rememberInfiniteTransition(label = "laser_anim")
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .border(2.dp, TeaGreenPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
    ) {
        // Laser Beam
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .align(Alignment.TopCenter)
                .offset(y = (scanProgress * 260).dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            TeaGreenPrimary.copy(alpha = 0.8f),
                            Color(0xFF38EF7D),
                            TeaGreenPrimary.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}
