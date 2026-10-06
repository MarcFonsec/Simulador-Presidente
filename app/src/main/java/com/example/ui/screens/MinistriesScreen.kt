package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SwapHoriz
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
import com.example.data.model.GameState
import com.example.data.model.MinisterProfile
import com.example.data.model.Ministry
import com.example.data.model.MinistryType
import com.example.ui.theme.*

@Composable
fun MinistriesScreen(
    state: GameState,
    onToggleProfile: (MinistryType) -> Unit,
    onUpdateBudget: (MinistryType, Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("ministries_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "ESPLANADA DOS MINISTÉRIOS",
                    style = MaterialTheme.typography.labelSmall,
                    color = PresidentialGoldLight,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Gestão de Custeio, Investimento e Articulação Ministerial",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }

        // Listagem dos 9 ministérios
        items(state.ministries) { ministry ->
            MinistryCard(
                ministry = ministry,
                onToggleProfile = { onToggleProfile(ministry.id) },
                onIncreaseInvest = { onUpdateBudget(ministry.id, 0.0, 0.10) },
                onDecreaseInvest = { onUpdateBudget(ministry.id, 0.0, -0.10) },
                onIncreaseCusteio = { onUpdateBudget(ministry.id, 0.10, 0.0) },
                onDecreaseCusteio = { onUpdateBudget(ministry.id, -0.10, 0.0) }
            )
        }
    }
}

@Composable
private fun MinistryCard(
    ministry: Ministry,
    onToggleProfile: () -> Unit,
    onIncreaseInvest: () -> Unit,
    onDecreaseInvest: () -> Unit,
    onIncreaseCusteio: () -> Unit,
    onDecreaseCusteio: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
            .testTag("ministry_card_${ministry.sigla.lowercase()}"),
        colors = CardDefaults.cardColors(containerColor = SlateNavyCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header do ministério
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${ministry.nome} (${ministry.sigla})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "Ministro(a): ${ministry.ministroNome}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PresidentialGoldLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Perfil do Ministro (Técnico vs Político)
                Surface(
                    color = if (ministry.perfil == MinisterProfile.TECNICO) 
                        InfoBlue.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, 
                        if (ministry.perfil == MinisterProfile.TECNICO) InfoBlue.copy(alpha = 0.5f) else WarningAmber.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = if (ministry.perfil == MinisterProfile.TECNICO) "TÉCNICO" else "POLÍTICO",
                        color = if (ministry.perfil == MinisterProfile.TECNICO) InfoBlue else WarningAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = ministry.descricaoPapel,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Indicador chave setorial
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateNavyDark, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = ministry.indicadorChaveNome,
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Text(
                    text = ministry.indicadorChaveValor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PresidentialGreenLight
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Eficiência e Satisfação da Categoria
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Eficiência: ${String.format(java.util.Locale.US, "%.0f", ministry.eficienciaSetorialPct)}%",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    LinearProgressIndicator(
                        progress = { (ministry.eficienciaSetorialPct.toFloat() / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = PresidentialGreenLight,
                        trackColor = SlateNavyDark
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Satisfação Categoria: ${String.format(java.util.Locale.US, "%.0f", ministry.satisfacaoCategoriaPct)}%",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    LinearProgressIndicator(
                        progress = { (ministry.satisfacaoCategoriaPct.toFloat() / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = PresidentialGold,
                        trackColor = SlateNavyDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Orçamento anual
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Investimento: R$ ${String.format(java.util.Locale.US, "%.1f", ministry.orcamentoInvestimentoAnualBrl / 1_000_000_000.0)} Bi",
                        fontSize = 11.sp,
                        color = TextWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Custeio: R$ ${String.format(java.util.Locale.US, "%.1f", ministry.orcamentoCusteioAnualBrl / 1_000_000_000.0)} Bi",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                // Botões de ação rápida
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = onDecreaseInvest,
                        modifier = Modifier
                            .size(34.dp)
                            .background(SlateBorder, RoundedCornerShape(6.dp))
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Diminuir Investimento", tint = TextWhite, modifier = Modifier.size(16.dp))
                    }

                    IconButton(
                        onClick = onIncreaseInvest,
                        modifier = Modifier
                            .size(34.dp)
                            .background(PresidentialGreenPrimary, RoundedCornerShape(6.dp))
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Aumentar Investimento", tint = TextWhite, modifier = Modifier.size(16.dp))
                    }

                    Button(
                        onClick = onToggleProfile,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SlateNavyDark,
                            contentColor = TextWhite
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Trocar Perfil", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
