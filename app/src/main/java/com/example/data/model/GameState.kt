package com.example.data.model

data class GameState(
    val mesAtual: Int = 1, // Mês 1 até 48 (Mandato de 4 anos)
    val anoAtual: Int = 2026,
    val economicMetrics: EconomicMetrics = EconomicMetrics(),
    val budgetMetrics: BudgetMetrics = BudgetMetrics(),
    val socialMetrics: SocialMetrics = SocialMetrics(),
    val politicalMetrics: PoliticalMetrics = PoliticalMetrics(),
    val ministries: List<Ministry> = emptyList(),
    val enterprises: List<StateEnterprise> = emptyList(),
    val bills: List<CongressionalBill> = emptyList(),
    val history: List<TurnHistoryItem> = emptyList(),
    val noticiasRecentes: List<String> = emptyList(),
    val eventoAtivo: GameEvent? = null,
    val fimDeJogo: Boolean = false,
    val motivoFimDeJogo: String? = null,
    val vitoria: Boolean = false,
    val buildVersion: String = "v1.0.26 - Brasil 2026 Engine"
) {
    val nomeMesAtual: String
        get() {
            val meses = listOf(
                "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
            )
            val index = (mesAtual - 1) % 12
            return "${meses[index]} de ${2026 + (mesAtual - 1) / 12}"
        }

    val mesesRestantes: Int
        get() = (48 - mesAtual).coerceAtLeast(0)
}
