package com.example.data.model

enum class EnterpriseRegime {
    ECONOMIA_MISTA,
    TOTALMENTE_PUBLICA,
    DEPENDENTE_TESOURO
}

enum class PricePolicy {
    PPI_INTERNACIONAL, // Segue o barril/dólar (alto lucro e dividendos, risco de inflação e greve)
    PARIDADE_INTERNA,   // Amortece preços (menor lucro, segura inflação a curto prazo)
    SUBSIDIO_DIRETO     // Preço controlado com prejuízo para estatal
}

data class StateEnterprise(
    val id: String,
    val nome: String,
    val setor: String,
    val regime: EnterpriseRegime,
    val participacaoUniaoVotoPct: Double,
    val lucroMedioAnualBrl: Double, // Positivo ou negativo (prejuízo)
    val dividendosRepassadosTesouroBrl: Double,
    val funcaoEstrategica: String,
    val politicaPreco: PricePolicy = PricePolicy.PPI_INTERNACIONAL,
    val privatizada: Boolean = false,
    val saneada: Boolean = false
)
