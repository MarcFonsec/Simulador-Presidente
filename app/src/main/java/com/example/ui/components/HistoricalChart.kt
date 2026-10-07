package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateNavyCard
import com.example.ui.theme.TextMuted

@Composable
fun HistoricalChart(
    title: String,
    dataPoints: List<Double>,
    unitSuffix: String,
    lineColor: Color,
    modifier: Modifier = Modifier,
    fillColor: Color = lineColor.copy(alpha = 0.15f)
) {
    Column(
        modifier = modifier
            .background(SlateNavyCard, RoundedCornerShape(12.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        val currentVal = dataPoints.lastOrNull() ?: 0.0
        val minVal = dataPoints.minOrNull() ?: 0.0
        val maxVal = dataPoints.maxOrNull() ?: (minVal + 1.0)
        val range = (maxVal - minVal).coerceAtLeast(0.1)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextMuted,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = String.format(java.util.Locale.US, "%.1f%s", currentVal, unitSuffix),
                style = MaterialTheme.typography.titleMedium,
                color = lineColor,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                if (dataPoints.size < 2) {
                    // Linha única reta
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, height / 2f),
                        end = Offset(width, height / 2f),
                        strokeWidth = 3.dp.toPx()
                    )
                    return@Canvas
                }

                // Grid lines horizontais (3 linhas)
                val gridLines = 3
                for (i in 0..gridLines) {
                    val y = height * (i.toFloat() / gridLines)
                    drawLine(
                        color = Color.White.copy(alpha = 0.08f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                val stepX = width / (dataPoints.size - 1)
                val path = Path()
                val fillPath = Path()

                dataPoints.forEachIndexed { index, value ->
                    val x = index * stepX
                    // Normalizado invertido (0 no topo, height na base)
                    val normalizedY = ((value - minVal) / range).toFloat()
                    val y = height - (normalizedY * (height - 16.dp.toPx()) + 8.dp.toPx())

                    if (index == 0) {
                        path.moveTo(x, y)
                        fillPath.moveTo(x, height)
                        fillPath.lineTo(x, y)
                    } else {
                        path.lineTo(x, y)
                        fillPath.lineTo(x, y)
                    }

                    // Ponto atual no último
                    if (index == dataPoints.size - 1) {
                        drawCircle(
                            color = lineColor,
                            radius = 4.dp.toPx(),
                            center = Offset(x, y)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 2.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }

                fillPath.lineTo(width, height)
                fillPath.close()

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(lineColor.copy(alpha = 0.35f), Color.Transparent),
                        startY = 0f,
                        endY = height
                    )
                )

                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = String.format(java.util.Locale.US, "Mín: %.1f%s", minVal, unitSuffix),
                fontSize = 11.sp,
                color = TextMuted
            )
            Text(
                text = "${dataPoints.size} meses acumulados",
                fontSize = 11.sp,
                color = TextMuted
            )
            Text(
                text = String.format(java.util.Locale.US, "Máx: %.1f%s", maxVal, unitSuffix),
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}
