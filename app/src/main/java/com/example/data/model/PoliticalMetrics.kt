package com.example.data.model

data class PoliticalMetrics(
    val apoioCongressoPct: Double = 54.0, // Base aliada no parlamento (Câmara e Senado)
    val deputadosBase: Int = 220,
    val deputadosCentrao: Int = 190,
    val deputadosOposicao: Int = 103,
    val capitalPolitico: Int = 85, // 0 a 100: consumido em PECs e reformas impopulares
    val indiceCorrupcaoPercebida: Double = 36.0, // 0 a 100 (CPI internacional, maior = mais limpo)
    val confiancaInvestidorPct: Double = 62.0,
    val riscoImpeachmentPct: Double = 8.0, // Função de popularidade <20% e congresso <33%
    val emendasLiberadasMesBrl: Double = 4_330_000_000.0, // R$ ~4.3 bi/mês (total ~52 bi/ano)
    val estabilidadeInstitucionalPct: Double = 70.0
) {
    val totalDeputados: Int = 513
}
