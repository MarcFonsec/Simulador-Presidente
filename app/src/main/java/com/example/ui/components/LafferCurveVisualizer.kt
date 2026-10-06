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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.pow

@Composable
fun LafferCurveVisualizer(
    taxRateCurrentPct: Double, // Ex: 33.8%
    optimalTaxRatePct: Double = 34.0, // Ponto ótimo τ*
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(SlateNavyCard, RoundedCornerShape(12.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Curva de Laffer & Eficiência Tributária",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Arrecadação R(τ) = Y · τ · (1 - ε(τ - τ*)²)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            val isInForbiddenZone = taxRateCurrentPct > 36.0
            val badgeColor = if (isInForbiddenZone) AlertRed else SuccessGreen
            val badgeText = if (isInForbiddenZone) "Zona de Evasão/Fuga" else "Faixa Sustentável"

            Box(
                modifier = Modifier
                    .background(badgeColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = badgeText,
                    color = badgeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Desenha a parábola da Curva de Laffer (de 0% a 70% de imposto)
                val curvePath = Path()
                val steps = 60
                val maxTax = 70.0

                var currentMarkerOffset = Offset.Zero
                var optimalMarkerOffset = Offset.Zero

                for (i in 0..steps) {
                    val t = (i.toDouble() / steps.toDouble()) * maxTax
                    // Parábola de Laffer normalizada: 0 em t=0 e decresce após t=optimalTax
                    // Fator = (t / 34) * (2 - (t / 34))
                    val normalizedRevenue = ((t / optimalTaxRatePct) * (2.0 - (t / optimalTaxRatePct))).coerceIn(0.0, 1.0)
                    val x = (t / maxTax).toFloat() * width
                    val y = height - (normalizedRevenue.toFloat() * (height - 18.dp.toPx()) + 8.dp.toPx())

                    if (i == 0) curvePath.moveTo(x, y) else curvePath.lineTo(x, y)

                    if (i == (steps * (optimalTaxRatePct / maxTax)).toInt()) {
                        optimalMarkerOffset = Offset(x, y)
                    }
                }

                // Linha de base
                drawLine(
                    color = Color.White.copy(alpha = 0.15f),
                    start = Offset(0f, height),
                    end = Offset(width, height),
                    strokeWidth = 1.dp.toPx()
                )

                // Desenha curva
                drawPath(
                    path = curvePath,
                    color = PresidentialGold,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Ponto ótimo
                if (optimalMarkerOffset != Offset.Zero) {
                    drawCircle(
                        color = SuccessGreen,
                        radius = 4.dp.toPx(),
                        center = optimalMarkerOffset
                    )
                }

                // Ponto atual
                val currentT = taxRateCurrentPct.coerceIn(0.0, maxTax)
                val currentNormRev = ((currentT / optimalTaxRatePct) * (2.0 - (currentT / optimalTaxRatePct))).coerceIn(0.0, 1.0)
                val currX = (currentT / maxTax).toFloat() * width
                val currY = height - (currentNormRev.toFloat() * (height - 18.dp.toPx()) + 8.dp.toPx())
                currentMarkerOffset = Offset(currX, currY)

                val markerColor = if (taxRateCurrentPct > 36.0) AlertRed else PresidentialGreenLight
                drawCircle(
                    color = markerColor,
                    radius = 6.dp.toPx(),
                    center = currentMarkerOffset
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = currentMarkerOffset
                )

                // Linha vertical pontilhada indicando a alíquota atual
                drawLine(
                    color = markerColor.copy(alpha = 0.5f),
                    start = currentMarkerOffset,
                    end = Offset(currX, height),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "0% (Receita Zero)",
                fontSize = 11.sp,
                color = TextMuted
            )
            Text(
                text = "τ* Ideal: ${String.format(java.util.Locale.US, "%.1f", optimalTaxRatePct)}%",
                fontSize = 11.sp,
                color = SuccessGreen,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Atual: ${String.format(java.util.Locale.US, "%.1f", taxRateCurrentPct)}%",
                fontSize = 11.sp,
                color = if (taxRateCurrentPct > 36.0) AlertRed else PresidentialGreenLight,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
