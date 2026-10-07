package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BillStatus
import com.example.data.model.BillType
import com.example.data.model.CongressionalBill
import com.example.data.model.GameState
import com.example.ui.theme.*

@Composable
fun CongressScreen(
    state: GameState,
    onLiberarEmendas: (Double) -> Unit,
    onSubmitBill: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pol = state.politicalMetrics

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("congress_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "CONGRESSO NACIONAL & GOVERNABILIDADE",
                    style = MaterialTheme.typography.labelSmall,
                    color = PresidentialGoldLight,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Articulação Política na Câmara (513 Deputados) e Votação de Reformas",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }

        // Composição Parlamentar
        item {
            CongressCompositionCard(state = state)
        }

        // Emendas Parlamentares
        item {
            AmendmentsManagementCard(
                state = state,
                onLiberarEmendas = onLiberarEmendas
            )
        }

        // Pauta Legislativa e Votação de Projetos
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PAUTA LEGISLATIVA DE REFORMAS",
                    style = MaterialTheme.typography.labelSmall,
                    color = PresidentialGoldLight,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )

                Surface(
                    color = PresidentialGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Capital Político: ${pol.capitalPolitico}/100",
                        color = PresidentialGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        items(state.bills) { bill ->
            BillItemCard(
                bill = bill,
                capitalPoliticoDisponivel = pol.capitalPolitico,
                onSubmit = { onSubmitBill(bill.id) }
            )
        }
    }
}

@Composable
private fun CongressCompositionCard(state: GameState) {
    val pol = state.politicalMetrics

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SlateNavyCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Câmara dos Deputados (513 Votos)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                Text(
                    text = "${String.format(java.util.Locale.US, "%.0f", pol.apoioCongressoPct)}% Base Aliada",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (pol.apoioCongressoPct >= 50.0) PresidentialGreenLight else AlertRed
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Barra segmentada da Câmara
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                Box(
                    modifier = Modifier
                        .weight(pol.deputadosBase.toFloat())
                        .fillMaxHeight()
                        .background(PresidentialGreenPrimary)
                )
                Box(
                    modifier = Modifier
                        .weight(pol.deputadosCentrao.toFloat())
                        .fillMaxHeight()
                        .background(WarningAmber)
                )
                Box(
                    modifier = Modifier
                        .weight(pol.deputadosOposicao.toFloat())
                        .fillMaxHeight()
                        .background(AlertRed)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BancadaLegend(
                    nome = "Base Fiel",
                    votos = "${pol.deputadosBase} dep.",
                    cor = PresidentialGreenPrimary
                )
                BancadaLegend(
                    nome = "Centrão (Fisiológico)",
                    votos = "${pol.deputadosCentrao} dep.",
                    cor = WarningAmber
                )
                BancadaLegend(
                    nome = "Oposição",
                    votos = "${pol.deputadosOposicao} dep.",
                    cor = AlertRed
                )
            }
        }
    }
}

@Composable
private fun BancadaLegend(nome: String, votos: String, cor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(cor, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(text = nome, fontSize = 11.sp, color = TextMuted)
            Text(text = votos, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        }
    }
}

@Composable
private fun AmendmentsManagementCard(
    state: GameState,
    onLiberarEmendas: (Double) -> Unit
) {
    val pol = state.politicalMetrics
    val valorMensalBi = pol.emendasLiberadasMesBrl / 1_000_000_000.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SlateNavyCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Emendas Parlamentares (Centrão)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = "R$ ${String.format(java.util.Locale.US, "%.1f", valorMensalBi)} Bi/mês",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PresidentialGoldLight
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Emendas compram apoio do Centrão para aprovar leis, mas consomem quase 30% do orçamento livre (discrecionário) da Presidência.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Slider(
                value = valorMensalBi.toFloat(),
                onValueChange = { onLiberarEmendas(it.toDouble() * 1_000_000_000.0) },
                valueRange = 1.0f..8.0f,
                colors = SliderDefaults.colors(
                    thumbColor = PresidentialGold,
                    activeTrackColor = PresidentialGoldLight,
                    inactiveTrackColor = SlateNavyDark
                ),
                modifier = Modifier.height(32.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "R$ 1 Bi (Rigor Fiscal)", fontSize = 10.sp, color = TextMuted)
                Text(text = "R$ 4.3 Bi (Média 2026)", fontSize = 10.sp, color = TextMuted)
                Text(text = "R$ 8 Bi (Fisiologismo)", fontSize = 10.sp, color = TextMuted)
            }
        }
    }
}

@Composable
private fun BillItemCard(
    bill: CongressionalBill,
    capitalPoliticoDisponivel: Int,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
            .testTag("bill_card_${bill.id}"),
        colors = CardDefaults.cardColors(containerColor = SlateNavyCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (bill.tipo) {
                        BillType.PEC -> AlertRed.copy(alpha = 0.2f)
                        BillType.LEI_COMPLEMENTAR -> WarningAmber.copy(alpha = 0.2f)
                        BillType.LEI_ORDINARIA -> InfoBlue.copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = when (bill.tipo) {
                            BillType.PEC -> "PEC (308 Votos / 60%)"
                            BillType.LEI_COMPLEMENTAR -> "Lei Complementar (257 Votos)"
                            BillType.LEI_ORDINARIA -> "Lei Ordinária (257 Votos)"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (bill.tipo) {
                            BillType.PEC -> AlertRed
                            BillType.LEI_COMPLEMENTAR -> WarningAmber
                            BillType.LEI_ORDINARIA -> InfoBlue
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                StatusPill(status = bill.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = bill.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = bill.descricao,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Impactos da Proposta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val impactoFiscal = bill.impactoFiscalAnualBrl / 1_000_000_000.0
                Text(
                    text = "Fiscal: ${if (impactoFiscal > 0) "+" else ""}${String.format(java.util.Locale.US, "%.1f", impactoFiscal)} Bi/ano",
                    fontSize = 11.sp,
                    color = if (impactoFiscal >= 0) PresidentialGreenLight else AlertRed,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Aprovação: ${if (bill.impactoAprovacaoGeral > 0) "+" else ""}${String.format(java.util.Locale.US, "%.1f", bill.impactoAprovacaoGeral)}%",
                    fontSize = 11.sp,
                    color = if (bill.impactoAprovacaoGeral >= 0) PresidentialGreenLight else AlertRed,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Custo Político: ${bill.impactoCapitalPoliticoNecessario} CP",
                    fontSize = 11.sp,
                    color = PresidentialGoldLight,
                    fontWeight = FontWeight.Bold
                )
            }

            if (bill.status == BillStatus.NA_GAVETA) {
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onSubmit,
                    enabled = capitalPoliticoDisponivel >= bill.impactoCapitalPoliticoNecessario,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PresidentialGreenPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    Icon(imageVector = Icons.Default.HowToVote, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Colocar em Votação no Plenário",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusPill(status: BillStatus) {
    val (text, color) = when (status) {
        BillStatus.NA_GAVETA -> "Na Gaveta" to TextMuted
        BillStatus.EM_TRAMITACAO -> "Em Tramitação" to WarningAmber
        BillStatus.APROVADA -> "Aprovada" to PresidentialGreenLight
        BillStatus.REJEITADA -> "Rejeitada" to AlertRed
    }

    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (status == BillStatus.APROVADA) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(3.dp))
            } else if (status == BillStatus.REJEITADA) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(3.dp))
            }
            Text(text = text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
