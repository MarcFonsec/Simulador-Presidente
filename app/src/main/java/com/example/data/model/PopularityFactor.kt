package com.example.data.model

data class PopularityFactor(
    val title: String,
    val impactPct: Double, // Ex: +4.5 ou -3.0
    val explanation: String,
    val category: String // "Inflação", "Emprego", "Fiscal", "Social", "Congresso", "Serviços"
) {
    val isPositive: Boolean
        get() = impactPct >= 0.0

    val formattedImpact: String
        get() {
            val prefix = if (impactPct > 0) "+" else ""
            return "$prefix${String.format(java.util.Locale.US, "%.1f", impactPct)}%"
        }
}
