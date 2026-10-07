package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameDifficulty
import com.example.data.model.GameState
import com.example.data.model.PopularityFactor
import com.example.ui.theme.*

@Composable
fun PopularityDiagnosisCard(
    state: GameState,
    onSelectDifficulty: (GameDifficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    val social = state.socialMetrics
    val factors = social.fatoresPopularidade

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SlateBorder, RoundedCornerShape(14.dp))
            .testTag("popularity_diagnosis_card"),
        colors = CardDefaults.cardColors(containerColor = SlateNavyCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Título & Seletor de Dificuldade
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = null,
                        tint = PresidentialGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FATORES DE POPULARIDADE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite
                    )
                }

                // Delta mensal
                val delta = social.variacaoUltimoMesPct
                if (state.mesAtual > 1) {
                    val deltaColor = if (delta >= 0) SuccessGreen else AlertRed
                    val deltaIcon = if (delta >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown
                    Surface(
                        color = deltaColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = deltaIcon,
                                contentDescription = null,
                                tint = deltaColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${if (delta > 0) "+" else ""}${String.format(java.util.Locale.US, "%.1f", delta)}% no mês",
                                color = deltaColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Text(
                text = "Diagnóstico em tempo real: o que está somando e subtraindo aprovação do governo",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Seletor de Modo de Dificuldade
            Text(
                text = "NÍVEL DE DIFICULDADE DA SIMULAÇÃO:",
                style = MaterialTheme.typography.labelSmall,
                color = PresidentialGoldLight,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                GameDifficulty.entries.forEach { diff ->
                    val isSelected = state.dificuldade == diff
                    Surface(
                        onClick = { onSelectDifficulty(diff) },
                        color = if (isSelected) PresidentialGold else SlateNavyDark,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) PresidentialGoldLight else SlateBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("diff_button_${diff.name.lowercase()}")
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (diff) {
                                    GameDifficulty.FACIL -> "Fácil"
                                    GameDifficulty.NORMAL -> "Normal"
                                    GameDifficulty.REALISTA_HARDCORE -> "Realista"
                                },
                                color = if (isSelected) Color.Black else TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Lista de Fatores em Tempo Real
            if (factors.isEmpty()) {
                Text(
                    text = "Aguardando avanço do primeiro ciclo mensal para cálculo detalhado.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    factors.forEach { factor ->
                        FactorRow(factor = factor)
                    }
                }
            }
        }
    }
}

@Composable
private fun FactorRow(factor: PopularityFactor) {
    val badgeColor = if (factor.isPositive) SuccessGreen else AlertRed
    val icon = if (factor.isPositive) Icons.Default.AddCircle else Icons.Default.RemoveCircle

    Surface(
        color = SlateNavyDark,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = badgeColor,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = factor.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        fontSize = 12.sp
                    )

                    Surface(
                        color = badgeColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = factor.formattedImpact,
                            color = badgeColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = factor.explanation,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
