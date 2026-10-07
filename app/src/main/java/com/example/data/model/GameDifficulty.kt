package com.example.data.model

enum class GameDifficulty(
    val title: String,
    val description: String,
    val approvalBonus: Double,
    val penaltyMultiplier: Double,
    val keynesianMultiplierBoost: Double,
    val maxMonthlyDrop: Double
) {
    FACIL(
        title = "Fácil (Tolerante)",
        description = "Alta tolerância da população e mercado. Reformas têm menor atrito e investimentos dão retorno acelerado.",
        approvalBonus = 12.0,
        penaltyMultiplier = 0.50,
        keynesianMultiplierBoost = 0.30,
        maxMonthlyDrop = 3.0
    ),
    NORMAL(
        title = "Normal (Equilibrado)",
        description = "Experiência balanceada. Decisões econômicas coerentes garantem estabilidade e reeleição viável.",
        approvalBonus = 5.0,
        penaltyMultiplier = 0.75,
        keynesianMultiplierBoost = 0.15,
        maxMonthlyDrop = 5.0
    ),
    REALISTA_HARDCORE(
        title = "Realista (Hardcore)",
        description = "Simulação estrita da economia política brasileira. Mercados e centrão exigem pulso firme.",
        approvalBonus = 0.0,
        penaltyMultiplier = 1.00,
        keynesianMultiplierBoost = 0.0,
        maxMonthlyDrop = 7.0
    )
}
