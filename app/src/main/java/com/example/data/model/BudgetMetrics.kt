package com.example.data.model

data class MandatorySpending(
    val previdenciaSocialInssBrl: Double = 1_120_000_000_000.0,
    val folhaFuncionalismoBrl: Double = 385_000_000_000.0,
    val pisoSaudeBrl: Double = 232_000_000_000.0,
    val pisoEducacaoBrl: Double = 182_000_000_000.0,
    val transferenciaRendaBrl: Double = 285_000_000_000.0,
    val subsidiosSubvencoesBrl: Double = 115_000_000_000.0
) {
    val totalBrl: Double
        get() = previdenciaSocialInssBrl + folhaFuncionalismoBrl + pisoSaudeBrl +
                pisoEducacaoBrl + transferenciaRendaBrl + subsidiosSubvencoesBrl
}

data class DiscretionarySpending(
    val infraestruturaInvestimentoBrl: Double = 78_000_000_000.0,
    val custeioOperacionalMinisteriosBrl: Double = 92_000_000_000.0,
    val defesaSegurancaInvestimentoBrl: Double = 18_000_000_000.0
) {
    val totalBrl: Double
        get() = infraestruturaInvestimentoBrl + custeioOperacionalMinisteriosBrl + defesaSegurancaInvestimentoBrl
}

data class BudgetMetrics(
    val gastosObrigatorios: MandatorySpending = MandatorySpending(),
    val gastosDiscrecionarios: DiscretionarySpending = DiscretionarySpending(),
    val emendasParlamentaresTotaisBrl: Double = 52_000_000_000.0,
    
    // Trabalho e Renda
    val salarioMinimoBrl: Double = 1621.0,
    val rendimentoMedioRealBrl: Double = 3777.0,
    val massaRendimentoMensalBrl: Double = 385_600_000_000.0,
    val taxaDesemprego: Double = 0.053, // 5.3%
    val taxaSubutilizacao: Double = 0.131,
    val taxaInformalidade: Double = 0.385, // 38.5%
    val populacaoOcupada: Long = 103_477_000L
) {
    val gastoTotalPrimarioBrl: Double
        get() = gastosObrigatorios.totalBrl + gastosDiscrecionarios.totalBrl + emendasParlamentaresTotaisBrl

    val percentualObrigatorio: Double
        get() = (gastosObrigatorios.totalBrl / gastoTotalPrimarioBrl) * 100.0

    val percentualDiscrecionario: Double
        get() = ((gastosDiscrecionarios.totalBrl + emendasParlamentaresTotaisBrl) / gastoTotalPrimarioBrl) * 100.0
}
