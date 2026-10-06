package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.components.HistoricalChart
import com.example.ui.components.MetricCard
import com.example.ui.components.PresidentialHeader
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    state: GameState,
    onAdvanceMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            PresidentialHeader(state = state, onAdvanceMonth = onAdvanceMonth)
        }

        // Seção: Métricas Econômicas Globais
        item {
            Text(
                text = "INDICADORES MACROECONÔMICOS",
                style = MaterialTheme.typography.labelSmall,
                color = PresidentialGoldLight,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "PIB Nominal",
                    value = "R$ ${String.format(java.util.Locale.US, "%.2f", state.economicMetrics.pibNominalBrl / 1_000_000_000_000.0)} Tri",
                    subtitle = "USD ${String.format(java.util.Locale.US, "%.2f", state.economicMetrics.pibUsd / 1_000_000_000_000.0)} Tri • +${String.format(java.util.Locale.US, "%.1f", state.economicMetrics.crescimentoPibProjAnual * 100)}% a.a.",
                    icon = Icons.Default.TrendingUp,
                    badgeText = "Y*",
                    badgeColor = PresidentialGreenLight,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Dívida Bruta (DBGG)",
                    value = "${String.format(java.util.Locale.US, "%.1f", state.economicMetrics.dividaBrutaPctPib * 100)}%",
                    subtitle = "R$ ${String.format(java.util.Locale.US, "%.1f", state.economicMetrics.dividaBrutaBrl / 1_000_000_000_000.0)} Tri do PIB",
                    icon = Icons.Default.AccountBalance,
                    badgeText = if (state.economicMetrics.dividaBrutaPctPib > 0.85) "Crítico" else "Alerta",
                    badgeColor = if (state.economicMetrics.dividaBrutaPctPib > 0.85) AlertRed else WarningAmber,
                    valueColor = if (state.economicMetrics.dividaBrutaPctPib > 0.85) AlertRed else TextWhite,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Taxa Selic Anual",
                    value = "${String.format(java.util.Locale.US, "%.2f", state.economicMetrics.taxaSelicAnual * 100)}%",
                    subtitle = "Juros da dívida: R$ ${String.format(java.util.Locale.US, "%.0f", state.economicMetrics.gastoAnualJurosDividaBrl / 1_000_000_000.0)} bi/ano",
                    icon = Icons.Default.Percent,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Inflação (IPCA 12m)",
                    value = "${String.format(java.util.Locale.US, "%.2f", state.economicMetrics.inflacaoIpca12m * 100)}%",
                    subtitle = "Meta BC: 3.00% • Phillips: u ${(state.budgetMetrics.taxaDesemprego * 100).toInt()}%",
                    icon = Icons.Default.ShowChart,
                    valueColor = if (state.economicMetrics.inflacaoIpca12m > 0.05) AlertRed else PresidentialGreenLight,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Câmbio (USD/BRL)",
                    value = "R$ ${String.format(java.util.Locale.US, "%.2f", state.economicMetrics.taxaCambioUsdBrl)}",
                    subtitle = "Reservas: USD 372 bi",
                    icon = Icons.Default.AttachMoney,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Risco-País (CDS)",
                    value = "${String.format(java.util.Locale.US, "%.0f", state.economicMetrics.custoRiscoPaisCdsBps)} bps",
                    subtitle = "Rating: ${state.economicMetrics.creditRating}",
                    icon = Icons.Default.Security,
                    badgeText = state.economicMetrics.creditRating.substringBefore(" "),
                    badgeColor = PresidentialGold,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Seção: Aprovação Popular por Classes Sociais
        item {
            Text(
                text = "APROVAÇÃO POPULAR POR CLASSE SOCIAL",
                style = MaterialTheme.typography.labelSmall,
                color = PresidentialGoldLight,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        item {
            ApprovalByClassCard(state = state)
        }

        // Gráficos Históricos de Evolução
        item {
            Text(
                text = "EVOLUÇÃO TEMPORAL (DINÂMICA DE SISTEMAS)",
                style = MaterialTheme.typography.labelSmall,
                color = PresidentialGoldLight,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        item {
            HistoricalChart(
                title = "Trajetória da Dívida Pública (% do PIB)",
                dataPoints = state.history.map { it.dividaPctPib },
                unitSuffix = "%",
                lineColor = if (state.economicMetrics.dividaBrutaPctPib > 0.85) AlertRed else WarningAmber
            )
        }

        item {
            HistoricalChart(
                title = "Aprovação Popular do Presidente (%)",
                dataPoints = state.history.map { it.aprovacaoPct },
                unitSuffix = "%",
                lineColor = PresidentialGreenLight
            )
        }

        // Notícias Recentes & Feed de Notificações
        item {
            Text(
                text = "DIÁRIO OFICIAL & NOTÍCIAS DE BRASÍLIA",
                style = MaterialTheme.typography.labelSmall,
                color = PresidentialGoldLight,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        items(state.noticiasRecentes) { noticia ->
            NewsFeedItem(noticia = noticia)
        }
    }
}

@Composable
private fun ApprovalByClassCard(state: GameState) {
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
                    text = "Aprovação Geral da República",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = "${String.format(java.util.Locale.US, "%.1f", state.socialMetrics.aprovacaoPopularGeralPct)}%",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = PresidentialGreenLight
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            ClassApprovalRow(
                classe = "Classe A/B (Alta renda)",
                criterio = "Mercado, inflação, sustentabilidade fiscal e tributação de capital",
                valor = state.socialMetrics.aprovacaoClasseABPct,
                cor = InfoBlue
            )

            Spacer(modifier = Modifier.height(10.dp))

            ClassApprovalRow(
                classe = "Classe C (Classe média)",
                criterio = "Emprego formal, serviços básicos, custo dos combustíveis e frete",
                valor = state.socialMetrics.aprovacaoClasseCPct,
                cor = PresidentialGoldLight
            )

            Spacer(modifier = Modifier.height(10.dp))

            ClassApprovalRow(
                classe = "Classe D/E (Baixa renda)",
                criterio = "Bolsa Família, preço da cesta básica e valor do salário mínimo",
                valor = state.socialMetrics.aprovacaoClasseDEPct,
                cor = PresidentialGreenLight
            )
        }
    }
}

@Composable
private fun ClassApprovalRow(
    classe: String,
    criterio: String,
    valor: Double,
    cor: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = classe,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Text(
                text = "${String.format(java.util.Locale.US, "%.1f", valor)}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = cor
            )
        }
        Text(
            text = criterio,
            fontSize = 10.sp,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (valor.toFloat() / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = cor,
            trackColor = SlateNavyDark
        )
    }
}

@Composable
private fun NewsFeedItem(noticia: String) {
    Surface(
        color = SlateNavyCard,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Newspaper,
                contentDescription = null,
                tint = PresidentialGold,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = noticia,
                fontSize = 12.sp,
                color = TextWhite,
                lineHeight = 16.sp
            )
        }
    }
}
