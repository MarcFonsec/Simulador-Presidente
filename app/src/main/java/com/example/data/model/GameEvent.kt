package com.example.data.model

enum class EventSeverity {
    INFO,
    ALERTA,
    CRISE_GRAVE,
    OPORTUNIDADE
}

data class EventOption(
    val texto: String,
    val descricaoImpacto: String,
    val impactoCustoBrl: Double = 0.0,
    val impactoAprovacaoGeral: Double = 0.0,
    val impactoAprovacaoAB: Double = 0.0,
    val impactoAprovacaoC: Double = 0.0,
    val impactoAprovacaoDE: Double = 0.0,
    val impactoApoioCongresso: Double = 0.0,
    val impactoCapitalPolitico: Int = 0,
    val impactoSelicBps: Double = 0.0,
    val impactoInflacaoBps: Double = 0.0,
    val impactoCdsBps: Double = 0.0,
    val impactoCambio: Double = 0.0
)

data class GameEvent(
    val id: String,
    val titulo: String,
    val descricao: String,
    val severidade: EventSeverity,
    val mesOcorrencia: Int,
    val opcoes: List<EventOption>,
    val resolvida: Boolean = false,
    val opcaoEscolhidaIndex: Int? = null
)
