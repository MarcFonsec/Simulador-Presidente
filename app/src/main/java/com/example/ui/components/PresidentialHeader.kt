package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.GameState
import com.example.ui.theme.*

@Composable
fun PresidentialHeader(
    state: GameState,
    onAdvanceMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
            .testTag("presidential_header"),
        colors = CardDefaults.cardColors(containerColor = SlateNavyCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Imagem de fundo do Palácio do Planalto
            Image(
                painter = painterResource(id = R.drawable.presidential_palace_1791328574884),
                contentDescription = "Palácio do Planalto - Sede do Governo Federal",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            // Gradiente escuro para garantir excelente legibilidade dos textos
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.45f),
                                SlateNavyCard.copy(alpha = 0.95f)
                            )
                        )
                    )
            )

            // Conteúdo sobreposto
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = PresidentialGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "GOVERNO DA REPÚBLICA",
                                style = MaterialTheme.typography.labelSmall,
                                color = PresidentialGoldLight,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = state.nomeMesAtual,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite
                        )
                        Text(
                            text = "Mês ${state.mesAtual} de 48 • ${state.buildVersion}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    // Botão Avançar Mês destacado
                    Button(
                        onClick = onAdvanceMonth,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PresidentialGreenPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .testTag("advance_month_button")
                            .height(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Avançar Mês (t+1)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Barra de progresso do mandato presidencial (48 meses)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Mandato Presidencial (2026 - 2029)",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "${state.mesesRestantes} meses restantes",
                            fontSize = 11.sp,
                            color = PresidentialGoldLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (state.mesAtual.toFloat() / 48f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = PresidentialGreenLight,
                        trackColor = SlateBorder
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Badges de Status Crítico (Rating, Impeachment, Base Aliada)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusBadge(
                        label = "Rating",
                        value = state.economicMetrics.creditRating.substringBefore(" "),
                        color = if (state.economicMetrics.custoRiscoPaisCdsBps > 300) AlertRed else SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatusBadge(
                        label = "Risco Impeachment",
                        value = "${String.format(java.util.Locale.US, "%.0f", state.politicalMetrics.riscoImpeachmentPct)}%",
                        color = if (state.politicalMetrics.riscoImpeachmentPct > 40) AlertRed else SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatusBadge(
                        label = "Base Congresso",
                        value = "${String.format(java.util.Locale.US, "%.0f", state.politicalMetrics.apoioCongressoPct)}%",
                        color = if (state.politicalMetrics.apoioCongressoPct < 40) WarningAmber else PresidentialGreenLight,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                color = TextMuted,
                maxLines = 1
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1
            )
        }
    }
}
