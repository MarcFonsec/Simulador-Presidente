package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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
import com.example.ui.components.LafferCurveVisualizer
import com.example.ui.components.MetricCard
import com.example.ui.theme.*

@Composable
fun EconomyScreen(
    state: GameState,
    onUpdateTax: (String, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val econ = state.economicMetrics
    val budget = state.budgetMetrics

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("economy_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "POLÍTICA FISCAL & MONETÁRIA",
                style = MaterialTheme.typography.labelSmall,
                color = PresidentialGoldLight,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        // Cartões de Resultado Fiscal
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Resultado Primário Anual",
                    value = "R$ ${String.format(java.util.Locale.US, "%.1f", econ.resultadoPrimarioAnualBrl / 1_000_000_000.0)} Bi",
                    subtitle = if (econ.resultadoPrimarioAnualBrl < 0) "Déficit Primário (Sem juros)" else "Superávit Primário",
                    valueColor = if (econ.resultadoPrimarioAnualBrl < 0) AlertRed else SuccessGreen,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Resultado Nominal Anual",
                    value = "R$ ${String.format(java.util.Locale.US, "%.1f", econ.resultadoNominalAnualBrl / 1_000_000_000.0)} Bi",
                    subtitle = "Inclui R$ ${String.format(java.util.Locale.US, "%.0f", econ.gastoAnualJurosDividaBrl / 1_000_000_000.0)} Bi de juros da Selic",
                    valueColor = AlertRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Curva de Laffer com visualizador
        item {
            LafferCurveVisualizer(
                taxRateCurrentPct = econ.cargaTributariaEfetivaPct * 100.0,
                optimalTaxRatePct = 34.0
            )
        }

        // Sliders de Alíquotas Tributárias
        item {
            TaxAdjustmentCard(
                state = state,
                onUpdateTax = onUpdateTax
            )
        }

        // Seção: O Nó Orçamentário (Gastos Rígidos vs Livres)
        item {
            Text(
                text = "O NÓ ORÇAMENTÁRIO FEDERAL",
                style = MaterialTheme.typography.labelSmall,
                color = PresidentialGoldLight,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        item {
            RigidBudgetCard(state = state)
        }

        // Custo do Refinanciamento e Sensibilidade da Selic
        item {
            SelicSensitivityCard(state = state)
        }
    }
}

@Composable
private fun TaxAdjustmentCard(
    state: GameState,
    onUpdateTax: (String, Double) -> Unit
) {
    val econ = state.economicMetrics

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SlateNavyCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Gestão de Alíquotas Tributárias Federais",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Text(
                text = "Alíquotas excessivas aumentam sonegação e fuga de capitais (Laffer). Alíquotas baixas ampliam o déficit fiscal.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Alíquota IR
            TaxSliderRow(
                label = "Imposto de Renda (Alíquota Média IR)",
                value = econ.aliquotaImpostoRenda,
                range = 0.15f..0.38f,
                onValueChange = { onUpdateTax("IR", it.toDouble()) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Alíquota Consumo (IBS/CBS)
            TaxSliderRow(
                label = "Tributação sobre Consumo (IVA Dual IBS/CBS)",
                value = econ.aliquotaConsumoIva,
                range = 0.18f..0.34f,
                onValueChange = { onUpdateTax("CONSUMO", it.toDouble()) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Alíquota Empresas (IRPJ + CSLL)
            TaxSliderRow(
                label = "Tributação sobre Empresas (IRPJ + CSLL)",
                value = econ.aliquotaEmpresas,
                range = 0.20f..0.44f,
                onValueChange = { onUpdateTax("EMPRESAS", it.toDouble()) }
            )
        }
    }
}

@Composable
private fun TaxSliderRow(
    label: String,
    value: Double,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextWhite
            )
            Text(
                text = "${String.format(java.util.Locale.US, "%.1f", value * 100)}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PresidentialGoldLight
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = PresidentialGold,
                activeTrackColor = PresidentialGoldLight,
                inactiveTrackColor = SlateNavyDark
            ),
            modifier = Modifier.height(32.dp)
        )
    }
}

@Composable
private fun RigidBudgetCard(state: GameState) {
    val b = state.budgetMetrics.gastosObrigatorios
    val d = state.budgetMetrics.gastosDiscrecionarios

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
                    text = "Divisão do Orçamento Primário",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = "93,5% Rígido vs 6,5% Livre",
                    style = MaterialTheme.typography.labelMedium,
                    color = WarningAmber,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Barra proporcional
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                Box(
                    modifier = Modifier
                        .weight(93.5f)
                        .fillMaxHeight()
                        .background(AlertRed.copy(alpha = 0.85f))
                )
                Box(
                    modifier = Modifier
                        .weight(6.5f)
                        .fillMaxHeight()
                        .background(PresidentialGreenLight)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Gastos Obrigatórios (Inflexíveis):",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AlertRed
            )
            BudgetItemRow("Previdência Social (INSS/RPPS)", "R$ 1.120 Bi (48% do total)")
            BudgetItemRow("Folha do Funcionalismo Federal", "R$ 385 Bi")
            BudgetItemRow("Piso Constitucional da Saúde", "R$ 232 Bi")
            BudgetItemRow("Piso Constitucional da Educação", "R$ 182 Bi")
            BudgetItemRow("Bolsa Família & Transferência de Renda", "R$ 285 Bi")
            BudgetItemRow("Subsídios e Subvenções", "R$ 115 Bi")

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Margem de Manobra Discrecionária:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PresidentialGreenLight
            )
            BudgetItemRow("Obras e Investimento em Infraestrutura", "R$ 78 Bi")
            BudgetItemRow("Custeio Operacional de Ministérios", "R$ 92 Bi")
            BudgetItemRow("Defesa e Segurança (Investimento)", "R$ 18 Bi")
            BudgetItemRow("Emendas Parlamentares do Congresso", "R$ 52 Bi")
        }
    }
}

@Composable
private fun BudgetItemRow(nome: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "• $nome", fontSize = 11.sp, color = TextMuted)
        Text(text = valor, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
    }
}

@Composable
private fun SelicSensitivityCard(state: GameState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SlateNavyDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PresidentialGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Regra Técnica: Efeito Juros da Selic na Dívida",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PresidentialGoldLight
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Com a Dívida Bruta em R$ 11,1 Trilhões, para cada +1,00% de aumento na taxa Selic definida pelo Banco Central, o custo anual do serviço da dívida sobe automaticamente em cerca de R$ 48 a 50 Bilhões por ano (R$ 4 bilhões a mais por mês apenas pagando juros).",
                style = MaterialTheme.typography.bodySmall,
                color = TextWhite,
                lineHeight = 17.sp
            )
        }
    }
}
