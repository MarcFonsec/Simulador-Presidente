package com.example.data.model

enum class BillType {
    LEI_ORDINARIA, // Exige 50% + 1 dos votos (> 257 deputados)
    LEI_COMPLEMENTAR, // Exige maioria absoluta (257 votos)
    PEC // Proposta de Emenda Constitucional: exige 60% (308 deputados) + Capital Político
}

enum class BillStatus {
    NA_GAVETA,
    EM_TRAMITACAO,
    APROVADA,
    REJEITADA
}

data class CongressionalBill(
    val id: String,
    val titulo: String,
    val tipo: BillType,
    val descricao: String,
    val impactoFiscalAnualBrl: Double, // Positivo = economia/arrecadação, Negativo = custo
    val impactoAprovacaoGeral: Double, // % variação na aprovação popular
    val impactoCapitalPoliticoNecessario: Int,
    val votosBaseEsperados: Int,
    val status: BillStatus = BillStatus.NA_GAVETA,
    val mesVotacao: Int? = null
) {
    val votosNecessarios: Int
        get() = when (tipo) {
            BillType.LEI_ORDINARIA -> 257
            BillType.LEI_COMPLEMENTAR -> 257
            BillType.PEC -> 308
        }
}
