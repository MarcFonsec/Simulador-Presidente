package com.example.data.model

data class EconomicMetrics(
    val pibNominalBrl: Double = 12_700_000_000_000.0, // R$ 12.7 trilhões
    val pibUsd: Double = 2_640_000_000_000.0, // USD 2.64 trilhões
    val pibPerCapitaBrl: Double = 60_500.0,
    val crescimentoPibProjAnual: Double = 0.025, // 2.5% a.a.
    val pibPotencialBrl: Double = 12_850_000_000_000.0,
    val taxaSelicAnual: Double = 0.1400, // 14.00%
    val inflacaoIpca12m: Double = 0.0422, // 4.22%
    val metaInflacaoBc: Double = 0.0300, // 3.00%
    val taxaCambioUsdBrl: Double = 5.42, // USD/BRL
    val reservasInternacionaisUsd: Double = 372_600_000_000.0,
    val balancaComercialSaldoUsd: Double = 75_000_000_000.0,
    
    // Fiscal e Dívida
    val dividaBrutaBrl: Double = 11_100_000_000_000.0, // DBGG
    val dividaBrutaPctPib: Double = 0.829, // 82.9%
    val dividaPublicaFederalBrl: Double = 9_290_000_000_000.0,
    val resultadoPrimarioAnualBrl: Double = -65_000_000_000.0, // Déficit primário
    val resultadoNominalAnualBrl: Double = -820_000_000_000.0, // Déficit nominal
    val gastoAnualJurosDividaBrl: Double = 755_000_000_000.0, // R$ 755 bi/ano
    val arrecadacaoFederalBrutaAnualBrl: Double = 2_650_000_000_000.0,
    val custoRiscoPaisCdsBps: Double = 210.0, // CDS 5 anos
    val creditRating: String = "BB- (Estável)",
    
    // Alíquotas tributárias médias (Curva de Laffer)
    val aliquotaImpostoRenda: Double = 0.275, // 27.5%
    val aliquotaConsumoIva: Double = 0.265, // 26.5% (IBS/CBS)
    val aliquotaEmpresas: Double = 0.340, // 34% (IRPJ + CSLL)
    val cargaTributariaEfetivaPct: Double = 0.338 // 33.8%
) {
    val hiatoProdutoPct: Double
        get() = ((pibNominalBrl - pibPotencialBrl) / pibPotencialBrl) * 100.0
}
