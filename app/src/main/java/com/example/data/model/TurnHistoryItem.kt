package com.example.data.model

data class TurnHistoryItem(
    val mes: Int,
    val anoCalendario: Int,
    val nomeMes: String,
    val pibTrilhoes: Double,
    val dividaPctPib: Double,
    val inflacaoPct: Double,
    val selicPct: Double,
    val aprovacaoPct: Double,
    val cambioUsd: Double,
    val riscoPaisCds: Double,
    val resultadoPrimarioBi: Double
)
