package com.example.data.model

enum class MinistryType {
    FAZENDA,
    SAUDE,
    EDUCACAO,
    DEFESA,
    JUSTICA,
    INFRAESTRUTURA,
    MEIO_AMBIENTE,
    RELACOES_EXTERIORES,
    DESENVOLVIMENTO_SOCIAL
}

enum class MinisterProfile {
    TECNICO,   // +Eficiência, -Corrupção, mas -Apoio Político com o Centrão
    POLITICO   // +Apoio no Congresso, -Eficiência, +Risco de escândalos
}

data class Ministry(
    val id: MinistryType,
    val nome: String,
    val sigla: String,
    val ministroNome: String,
    val perfil: MinisterProfile,
    val orcamentoCusteioAnualBrl: Double,
    val orcamentoInvestimentoAnualBrl: Double,
    val eficienciaSetorialPct: Double, // 0 a 100
    val satisfacaoCategoriaPct: Double, // 0 a 100
    val indicadorChaveNome: String,
    val indicadorChaveValor: String,
    val descricaoPapel: String
) {
    val orcamentoTotalAnualBrl: Double
        get() = orcamentoCusteioAnualBrl + orcamentoInvestimentoAnualBrl
}
