package com.example.data.model

data class SocialMetrics(
    val aprovacaoPopularGeralPct: Double = 48.0, // 0 a 100%
    val aprovacaoClasseABPct: Double = 42.0, // Mercado, inflação, responsabilidade fiscal, capital
    val aprovacaoClasseCPct: Double = 49.0, // Emprego formal, serviços básicos, custo de vida
    val aprovacaoClasseDEPct: Double = 55.0, // Cesta básica, Bolsa Família, salário mínimo
    val indiceGini: Double = 0.518,
    val idh: Double = 0.760,
    val custoCestaBasicaBrl: Double = 815.0,
    val familiasEndividadasPct: Double = 71.4,
    val familiasInadimplentesPct: Double = 28.8,
    val taxaHomicidiosPor100k: Double = 18.2,
    val riscoProtestoSocialPct: Double = 15.0, // Gatilho para manifestações e greves
    val populacaoTotal: Long = 216_400_000L
)
