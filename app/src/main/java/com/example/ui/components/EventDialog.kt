package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.EventOption
import com.example.data.model.EventSeverity
import com.example.data.model.GameEvent
import com.example.ui.theme.*

@Composable
fun EventDialog(
    event: GameEvent,
    onSelectOption: (Int) -> Unit
) {
    Dialog(onDismissRequest = { /* Modal forces player decision */ }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, if (event.severidade == EventSeverity.CRISE_GRAVE) AlertRed else PresidentialGold, RoundedCornerShape(16.dp))
                .testTag("event_dialog"),
            colors = CardDefaults.cardColors(containerColor = SlateNavyDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header do evento com severidade
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val severityColor = when (event.severidade) {
                        EventSeverity.CRISE_GRAVE -> AlertRed
                        EventSeverity.ALERTA -> WarningAmber
                        EventSeverity.OPORTUNIDADE -> SuccessGreen
                        EventSeverity.INFO -> InfoBlue
                    }
                    val severityText = when (event.severidade) {
                        EventSeverity.CRISE_GRAVE -> "CRISE NACIONAL"
                        EventSeverity.ALERTA -> "ALERTA POLÍTICO"
                        EventSeverity.OPORTUNIDADE -> "OPORTUNIDADE ESTRATÉGICA"
                        EventSeverity.INFO -> "COMUNICADO"
                    }

                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = severityColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = severityColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = severityText,
                            color = severityColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = event.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = event.descricao,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "AÇÕES PRESIDENCIAIS DISPONÍVEIS:",
                    style = MaterialTheme.typography.labelSmall,
                    color = PresidentialGoldLight,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Opções
                event.opcoes.forEachIndexed { index, option ->
                    OptionCard(
                        option = option,
                        onClick = { onSelectOption(index) },
                        index = index
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun OptionCard(
    option: EventOption,
    onClick: () -> Unit,
    index: Int
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
            .testTag("event_option_$index"),
        color = SlateNavyCard,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "${index + 1}. ${option.texto}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = option.descricaoImpacto,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                fontSize = 12.sp
            )
        }
    }
}
