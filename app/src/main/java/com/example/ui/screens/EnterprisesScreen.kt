package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EnterpriseRegime
import com.example.data.model.GameState
import com.example.data.model.PricePolicy
import com.example.data.model.StateEnterprise
import com.example.ui.theme.*

@Composable
fun EnterprisesScreen(
    state: GameState,
    onSetPricePolicy: (String, PricePolicy) -> Unit,
    onRestructureEnterprise: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("enterprises_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "EMPRESAS ESTATAIS FEDERAIS",
                    style = MaterialTheme.typography.labelSmall,
                    color = PresidentialGoldLight,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "170 Estatais • Petrobras, Bancos Públicos e Bloco Dependente do Tesouro",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }

        // Cartão resumo de dividendos e custo fiscal
        item {
            val dividendosTotais = state.enterprises.sumOf { if (!it.privatizada) it.dividendosRepassadosTesouroBrl else 0.0 }
            val custoDependentes = state.enterprises.filter { it.regime == EnterpriseRegime.DEPENDENTE_TESOURO && !it.saneada }
                .sumOf { -it.lucroMedioAnualBrl }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SlateBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = SlateNavyCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Dividendos para o Tesouro", fontSize = 11.sp, color = TextMuted)
                        Text(
                            text = "R$ ${String.format(java.util.Locale.US, "%.1f", dividendosTotais / 1_000_000_000.0)} Bi/ano",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PresidentialGreenLight
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Custo Estatais Dependentes", fontSize = 11.sp, color = TextMuted)
                        Text(
                            text = "R$ ${String.format(java.util.Locale.US, "%.1f", custoDependentes / 1_000_000_000.0)} Bi/ano",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AlertRed
                        )
                    }
                }
            }
        }

        items(state.enterprises) { enterprise ->
            EnterpriseItemCard(
                enterprise = enterprise,
                onSetPricePolicy = { onSetPricePolicy(enterprise.id, it) },
                onRestructure = { onRestructureEnterprise(enterprise.id) }
            )
        }
    }
}

@Composable
private fun EnterpriseItemCard(
    enterprise: StateEnterprise,
    onSetPricePolicy: (PricePolicy) -> Unit,
    onRestructure: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
            .testTag("enterprise_card_${enterprise.id}"),
        colors = CardDefaults.cardColors(containerColor = SlateNavyCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = enterprise.nome,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "${enterprise.setor} • Participação da União: ${String.format(java.util.Locale.US, "%.1f", enterprise.participacaoUniaoVotoPct)}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                if (enterprise.saneada || enterprise.privatizada) {
                    Surface(
                        color = PresidentialGreenLight.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = PresidentialGreenLight, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = "SANEADA", color = PresidentialGreenLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = enterprise.funcaoEstrategica,
                style = MaterialTheme.typography.bodySmall,
                color = TextWhite,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Resultado Financeiro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val lucroBi = enterprise.lucroMedioAnualBrl / 1_000_000_000.0
                Text(
                    text = if (lucroBi >= 0) "Lucro Médio: R$ ${String.format(java.util.Locale.US, "%.1f", lucroBi)} Bi"
                    else "Déficit Anual: R$ ${String.format(java.util.Locale.US, "%.1f", lucroBi)} Bi",
                    fontSize = 11.sp,
                    color = if (lucroBi >= 0) PresidentialGreenLight else AlertRed,
                    fontWeight = FontWeight.Bold
                )

                if (enterprise.dividendosRepassadosTesouroBrl > 0) {
                    Text(
                        text = "Dividendos: R$ ${String.format(java.util.Locale.US, "%.1f", enterprise.dividendosRepassadosTesouroBrl / 1_000_000_000.0)} Bi/ano",
                        fontSize = 11.sp,
                        color = PresidentialGoldLight,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Opção específica da Petrobras: Política de Preços
            if (enterprise.id == "petrobras") {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Política de Preços dos Combustíveis (Diesel/Gasolina):",
                    fontSize = 11.sp,
                    color = PresidentialGoldLight,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PricePolicyButton(
                        text = "PPI Internacional",
                        selected = enterprise.politicaPreco == PricePolicy.PPI_INTERNACIONAL,
                        onClick = { onSetPricePolicy(PricePolicy.PPI_INTERNACIONAL) },
                        modifier = Modifier.weight(1f)
                    )
                    PricePolicyButton(
                        text = "Paridade Interna",
                        selected = enterprise.politicaPreco == PricePolicy.PARIDADE_INTERNA,
                        onClick = { onSetPricePolicy(PricePolicy.PARIDADE_INTERNA) },
                        modifier = Modifier.weight(1f)
                    )
                    PricePolicyButton(
                        text = "Preço Controlado",
                        selected = enterprise.politicaPreco == PricePolicy.SUBSIDIO_DIRETO,
                        onClick = { onSetPricePolicy(PricePolicy.SUBSIDIO_DIRETO) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Opção para Estatais Dependentes ou Correios com déficit
            if ((enterprise.id == "dependentes_tesouro" || enterprise.id == "correios") && !enterprise.saneada) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onRestructure,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PresidentialGreenPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                ) {
                    Text(
                        text = if (enterprise.id == "dependentes_tesouro") "Extinguir e Fundir Cabides (Economizar R$ 25 Bi)" 
                        else "Executar Plano de Recuperação e Reestruturação",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PricePolicyButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = if (selected) PresidentialGold else SlateNavyDark,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) PresidentialGoldLight else SlateBorder
        ),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (selected) Color.Black else TextWhite,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}
