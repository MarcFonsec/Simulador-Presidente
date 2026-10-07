package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.GameDifficulty
import com.example.data.model.GameState
import com.example.ui.theme.*

@Composable
fun GameOverDialog(
    state: GameState,
    onRestartWithDifficulty: (GameDifficulty) -> Unit
) {
    var selectedDifficulty by remember { mutableStateOf(state.dificuldade) }

    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, if (state.vitoria) PresidentialGold else AlertRed, RoundedCornerShape(16.dp))
                .testTag("game_over_dialog"),
            colors = CardDefaults.cardColors(containerColor = SlateNavyDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (state.vitoria) Icons.Default.EmojiEvents else Icons.Default.Gavel,
                    contentDescription = null,
                    tint = if (state.vitoria) PresidentialGold else AlertRed,
                    modifier = Modifier.size(54.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (state.vitoria) "MANDATO CONCLUÍDO!" else "FIM DE GOVERNO",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (state.vitoria) PresidentialGoldLight else AlertRed
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = state.motivoFimDeJogo ?: "Mandato encerrado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhite,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Resumo do Legado Presidencial
                Surface(
                    color = SlateNavyCard,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "ESTATÍSTICAS FINAIS",
                            style = MaterialTheme.typography.labelSmall,
                            color = PresidentialGoldLight,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        StatRow("Duração:", "${state.mesAtual} meses")
                        StatRow("Aprovação Popular Final:", "${String.format(java.util.Locale.US, "%.1f", state.socialMetrics.aprovacaoPopularGeralPct)}%")
                        StatRow("Dívida Bruta / PIB:", "${String.format(java.util.Locale.US, "%.1f", state.economicMetrics.dividaBrutaPctPib * 100)}%")
                        StatRow("Taxa Selic Final:", "${String.format(java.util.Locale.US, "%.2f", state.economicMetrics.taxaSelicAnual * 100)}%")
                        StatRow("Inflação IPCA:", "${String.format(java.util.Locale.US, "%.2f", state.economicMetrics.inflacaoIpca12m * 100)}%")
                        StatRow("Classificação de Risco:", state.economicMetrics.creditRating)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "ESCOLHA A DIFICULDADE DO NOVO MANDATO:",
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
                        val isSelected = selectedDifficulty == diff
                        Surface(
                            onClick = { selectedDifficulty = diff },
                            color = if (isSelected) PresidentialGold else SlateNavyCard,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) PresidentialGoldLight else SlateBorder
                            ),
                            modifier = Modifier.weight(1f)
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
                                    color = if (isSelected) androidx.compose.ui.graphics.Color.Black else TextWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = { onRestartWithDifficulty(selectedDifficulty) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PresidentialGreenPrimary,
                        contentColor = androidx.compose.ui.graphics.Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("restart_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Iniciar Novo Mandato Presidencial",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = TextMuted)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
    }
}
