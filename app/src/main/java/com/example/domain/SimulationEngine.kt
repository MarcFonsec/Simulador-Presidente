package com.example.domain

import com.example.data.model.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

object SimulationEngine {

    fun advanceMonth(currentState: GameState): GameState {
        if (currentState.fimDeJogo) return currentState

        val novoMes = currentState.mesAtual + 1
        val novoAno = 2026 + (novoMes - 1) / 12

        var econ = currentState.economicMetrics
        var budget = currentState.budgetMetrics
        var social = currentState.socialMetrics
        var pol = currentState.politicalMetrics
        var ministries = currentState.ministries
        var enterprises = currentState.enterprises
        val logsNoticias = mutableListOf<String>()

        // 1. CÁLCULO DA ARRECADAÇÃO (Curva de Laffer)
        // Ponto ideal tau* = 34.0%
        val tau = (econ.aliquotaImpostoRenda * 0.35 + econ.aliquotaConsumoIva * 0.40 + econ.aliquotaEmpresas * 0.25)
        val tauIdeal = 0.34
        val distLaffer = tau - tauIdeal
        // Fator de eficiência de Laffer (1 - 1.8 * (tau - tau*)^2)
        val lafferFactor = (1.0 - 1.8 * distLaffer.pow(2)).coerceIn(0.70, 1.05)
        
        // Dividendos recebidos das estatais ativas
        val dividendosMensaisEstatais = enterprises.sumOf { 
            if (!it.privatizada) it.dividendosRepassadosTesouroBrl / 12.0 else 0.0 
        }

        // Custo mensal das estatais dependentes
        val custoMensalDependentes = enterprises
            .filter { it.regime == EnterpriseRegime.DEPENDENTE_TESOURO && !it.saneada }
            .sumOf { -it.lucroMedioAnualBrl / 12.0 }

        val pibMensal = econ.pibNominalBrl / 12.0
        val arrecadacaoMensalBase = (pibMensal * tau * lafferFactor) + dividendosMensaisEstatais

        // 2. GASTOS DO MÊS
        // Gastos Obrigatórios (Previdência, Folha, Pisos, Bolsa Família)
        val gastosObrigMensais = (budget.gastosObrigatorios.totalBrl / 12.0) + custoMensalDependentes
        
        // Gastos Discrecionários (Infraestrutura, Custeio e Defesa)
        val investimentosMinisteriosMensal = ministries.sumOf { it.orcamentoInvestimentoAnualBrl / 12.0 }
        val custeioMinisteriosMensal = ministries.sumOf { it.orcamentoCusteioAnualBrl / 12.0 }
        val gastosDiscrecMensais = investimentosMinisteriosMensal + custeioMinisteriosMensal
        
        // Emendas parlamentares mensais
        val emendasMensais = pol.emendasLiberadasMesBrl

        val gastoPrimarioMensal = gastosObrigMensais + gastosDiscrecMensais + emendasMensais

        // 3. RESULTADO PRIMÁRIO E NOMINAL
        val resultadoPrimarioMensal = arrecadacaoMensalBase - gastoPrimarioMensal
        val resultadoPrimarioAnualProjetado = resultadoPrimarioMensal * 12.0

        // Juros da Dívida e impacto da Selic
        // Cada +1.0% de Selic = ~R$ 48 bilhões/ano (R$ 4 bi/mês)
        val custoJurosMensal = (econ.dividaBrutaBrl * (econ.taxaSelicAnual * 0.72 + 0.038 * 0.28)) / 12.0
        val resultadoNominalMensal = resultadoPrimarioMensal - custoJurosMensal
        val resultadoNominalAnualProjetado = resultadoNominalMensal * 12.0

        // Dívida Bruta atualizada
        val novaDividaBruta = econ.dividaBrutaBrl - resultadoNominalMensal
        val novoPib = calculateNewGdp(econ, budget, gastoPrimarioMensal, tau, distLaffer)
        val novaDividaPctPib = (novaDividaBruta / novoPib).coerceAtLeast(0.40)

        // 4. RISCO-PAÍS (CDS 5 ANOS) & RATING
        val baseCds = 210.0
        val fatorDivida = max(0.0, (novaDividaPctPib - 0.75) * 800.0)
        val fatorDeficit = max(0.0, (-resultadoNominalAnualProjetado - 700_000_000_000.0) / 10_000_000_000.0) * 1.5
        val fatorInstabilidade = (100.0 - pol.estabilidadeInstitucionalPct) * 1.2
        val novoCds = (baseCds + fatorDivida + fatorDeficit + fatorInstabilidade).coerceIn(120.0, 850.0)

        val novoRating = when {
            novoCds < 160 -> "BBB- (Grau de Investimento)"
            novoCds < 220 -> "BB- (Estável)"
            novoCds < 320 -> "B+ (Risco Moderado)"
            novoCds < 500 -> "CCC (Alerta de Degradação)"
            else -> "D (Risco Iminente de Calote/Default)"
        }

        // 5. CÂMBIO (USD / BRL) & INFLAÇÃO (IPCA) & CURVA DE PHILLIPS
        // Câmbio puxado por CDS e diferencial de juros
        val taxaCambioBase = 5.42
        val variacaoCds = (novoCds - 210.0) / 100.0 * 0.28
        val novoCambio = (taxaCambioBase + variacaoCds).coerceIn(4.60, 7.50)

        // Curva de Phillips: u natural = 5.5%
        val desemprego = budget.taxaDesemprego
        val pressaoPhillips = max(0.0, (0.055 - desemprego) * 0.35)
        
        // Pass-through cambial
        val passThroughCambial = max(0.0, (novoCambio - 5.42) * 0.008)
        
        // Custo logístico da infraestrutura
        val minInfra = ministries.find { it.id == MinistryType.INFRAESTRUTURA }
        val eficienciaInfra = minInfra?.eficienciaSetorialPct ?: 65.0
        val freteInflacao = if (eficienciaInfra < 60.0) 0.004 else -0.002

        val novaInflacaoIpca = (econ.inflacaoIpca12m + pressaoPhillips + passThroughCambial + freteInflacao)
            .coerceIn(0.020, 0.25)

        // 6. BANCO CENTRAL & TAXA SELIC (Regra de Taylor / Autonomia)
        val metaInflacao = econ.metaInflacaoBc
        var novaSelic = econ.taxaSelicAnual
        if (novaInflacaoIpca > metaInflacao + 0.015) {
            // Inflação estourando o teto: BC sobe juros em +0.25% a cada 2 meses
            if (novoMes % 2 == 0) {
                novaSelic = min(0.24, novaSelic + 0.0025)
                logsNoticias.add("Banco Central (Copom): Elevação da taxa Selic para ${(novaSelic * 100).format(2)}% para combater inflação de ${(novaInflacaoIpca * 100).format(2)}%.")
            }
        } else if (novaInflacaoIpca < metaInflacao + 0.005 && novaSelic > 0.10) {
            // Inflação controlada: BC afrouxa juros gradualmente
            if (novoMes % 3 == 0) {
                novaSelic = max(0.08, novaSelic - 0.0025)
                logsNoticias.add("Banco Central (Copom): Redução da taxa Selic para ${(novaSelic * 100).format(2)}% com ancoragem das expectativas.")
            }
        }

        // 7. MERCADO DE TRABALHO & RENDA
        val novoDesemprego = (desemprego + (novaSelic - 0.13) * 0.02 - (novoPib / econ.pibNominalBrl - 1.0) * 0.3)
            .coerceIn(0.038, 0.14)
        val novaCestaBasica = budget.salarioMinimoBrl * 0.50 * (1.0 + novaInflacaoIpca)

        // 8. POPULARIDADE POR CLASSE SOCIAL
        // Classe A/B: odeia inflação alta, dívida descontrolada e impostos de empresas
        var scoreAB = 42.0 - (novaInflacaoIpca - 0.04) * 200.0 - (novaDividaPctPib - 0.80) * 120.0 - (econ.aliquotaEmpresas - 0.34) * 100.0
        scoreAB = scoreAB.coerceIn(5.0, 95.0)

        // Classe C: sensível a desemprego, cesta básica, segurança pública e frete
        val minJustica = ministries.find { it.id == MinistryType.JUSTICA }
        val efJustica = minJustica?.eficienciaSetorialPct ?: 70.0
        var scoreC = 49.0 - (novoDesemprego - 0.05) * 250.0 - (novaInflacaoIpca - 0.04) * 150.0 + (efJustica - 70.0) * 0.4
        scoreC = scoreC.coerceIn(5.0, 95.0)

        // Classe D/E: sensível a Bolsa Família, salário mínimo e preço de alimentos
        val minSocial = ministries.find { it.id == MinistryType.DESENVOLVIMENTO_SOCIAL }
        val efSocial = minSocial?.eficienciaSetorialPct ?: 75.0
        var scoreDE = 55.0 - (novaInflacaoIpca - 0.04) * 180.0 + (efSocial - 70.0) * 0.5 - (novoDesemprego - 0.05) * 150.0
        scoreDE = scoreDE.coerceIn(5.0, 95.0)

        val aprovacaoGeral = (0.15 * scoreAB + 0.45 * scoreC + 0.40 * scoreDE).coerceIn(5.0, 95.0)

        // 9. CONGRESSO NACIONAL & GOVERNABILIDADE
        // Centrão quer emendas e cargos
        val percentualMinistrosPoliticos = ministries.count { it.perfil == MinisterProfile.POLITICO }.toDouble() / ministries.size.toDouble()
        val emendasFator = (pol.emendasLiberadasMesBrl / 4_330_000_000.0).coerceIn(0.5, 2.0)
        
        // Apoio dos parlamentares do Centrão
        val apoioCentraoPct = (45.0 + (emendasFator - 1.0) * 30.0 + (percentualMinistrosPoliticos - 0.5) * 35.0 + (aprovacaoGeral - 40.0) * 0.3)
            .coerceIn(10.0, 90.0)
        val deputadosCentraoApoiando = (pol.deputadosCentrao * (apoioCentraoPct / 100.0)).toInt()
        val totalApoioDeputados = pol.deputadosBase + deputadosCentraoApoiando
        val novoApoioCongressoPct = ((totalApoioDeputados.toDouble() / pol.totalDeputados.toDouble()) * 100.0).coerceIn(20.0, 90.0)

        // Risco de Impeachment: A_pop < 22% E A_congresso < 33%
        var riscoImpeachment = pol.riscoImpeachmentPct
        if (aprovacaoGeral < 22.0 && novoApoioCongressoPct < 33.0) {
            riscoImpeachment = min(100.0, riscoImpeachment + 15.0)
            logsNoticias.add("⚠️ ALERTA EM BRASÍLIA: Oposição articula pedido de Impeachment diante de colapso de popularidade e falta de base!")
        } else if (aprovacaoGeral > 40.0 && novoApoioCongressoPct > 50.0) {
            riscoImpeachment = max(0.0, riscoImpeachment - 5.0)
        }

        // Risco de golpe militar se Defesa estiver sucateada
        val minDefesa = ministries.find { it.id == MinistryType.DEFESA }
        val lealdadeMilitar = minDefesa?.satisfacaoCategoriaPct ?: 80.0
        if (lealdadeMilitar < 35.0) {
            logsNoticias.add("⚠️ CRISE NOS QUARTÉIS: Descontentamento militar atinge nível crítico após cortes no orçamento de defesa.")
        }

        // Geração de Evento Aleatório ou Disparado por Gatilho
        val evento = checkForEvents(novoMes, novoCambio, novaInflacaoIpca, novoDesemprego, ministries, enterprises)

        // News logs
        if (novoMes % 3 == 0) {
            logsNoticias.add("Fechamento Trimestral: Dívida Bruta em ${(novaDividaPctPib * 100).format(1)}% do PIB e Aprovação Popular em ${aprovacaoGeral.format(1)}%.")
        }
        if (novaDividaPctPib > 0.88) {
            logsNoticias.add("Agências de Risco Internacional rebaixam perspectiva dos títulos soberanos do Brasil.")
        }

        // Checagem de Fim de Jogo
        var fimDeJogo = false
        var motivoFim: String? = null
        var vitoria = false

        if (riscoImpeachment >= 95.0) {
            fimDeJogo = true
            motivoFim = "IMPEACHMENT: Aprovado pelo Congresso Nacional por perda total de base e colapso na aprovação popular."
        } else if (lealdadeMilitar < 20.0) {
            fimDeJogo = true
            motivoFim = "RUPTURA INSTITUCIONAL: Crise militar grave culminou na deposição do governo por desestabilização da Defesa."
        } else if (novaDividaPctPib > 1.05 && novoCds > 700.0) {
            fimDeJogo = true
            motivoFim = "DEFAULT & MORATÓRIA: O país declarou calote na dívida pública com fuga massiva de capitais e hiperinflação."
        } else if (novoMes >= 48) {
            fimDeJogo = true
            vitoria = true
            motivoFim = "FIM DO MANDATO (48 Meses): Você completou os 4 anos de governo republicano! O país manteve as instituições e estabilidade democrática."
        }

        // Histórico
        val novoItemHistorico = TurnHistoryItem(
            mes = novoMes,
            anoCalendario = novoAno,
            nomeMes = getNomeMes(novoMes),
            pibTrilhoes = novoPib / 1_000_000_000_000.0,
            dividaPctPib = novaDividaPctPib * 100.0,
            inflacaoPct = novaInflacaoIpca * 100.0,
            selicPct = novaSelic * 100.0,
            aprovacaoPct = aprovacaoGeral,
            cambioUsd = novoCambio,
            riscoPaisCds = novoCds,
            resultadoPrimarioBi = resultadoPrimarioAnualProjetado / 1_000_000_000.0
        )

        return currentState.copy(
            mesAtual = novoMes,
            anoAtual = novoAno,
            economicMetrics = econ.copy(
                pibNominalBrl = novoPib,
                taxaSelicAnual = novaSelic,
                inflacaoIpca12m = novaInflacaoIpca,
                taxaCambioUsdBrl = novoCambio,
                dividaBrutaBrl = novaDividaBruta,
                dividaBrutaPctPib = novaDividaPctPib,
                resultadoPrimarioAnualBrl = resultadoPrimarioAnualProjetado,
                resultadoNominalAnualBrl = resultadoNominalAnualProjetado,
                gastoAnualJurosDividaBrl = custoJurosMensal * 12.0,
                arrecadacaoFederalBrutaAnualBrl = arrecadacaoMensalBase * 12.0,
                custoRiscoPaisCdsBps = novoCds,
                creditRating = novoRating,
                cargaTributariaEfetivaPct = tau
            ),
            budgetMetrics = budget.copy(
                taxaDesemprego = novoDesemprego
            ),
            socialMetrics = social.copy(
                aprovacaoPopularGeralPct = aprovacaoGeral,
                aprovacaoClasseABPct = scoreAB,
                aprovacaoClasseCPct = scoreC,
                aprovacaoClasseDEPct = scoreDE,
                custoCestaBasicaBrl = novaCestaBasica
            ),
            politicalMetrics = pol.copy(
                apoioCongressoPct = novoApoioCongressoPct,
                riscoImpeachmentPct = riscoImpeachment,
                capitalPolitico = (pol.capitalPolitico + 2).coerceAtMost(100)
            ),
            history = currentState.history + novoItemHistorico,
            noticiasRecentes = logsNoticias + currentState.noticiasRecentes.take(6),
            eventoAtivo = evento ?: currentState.eventoAtivo,
            fimDeJogo = fimDeJogo,
            motivoFimDeJogo = motivoFim,
            vitoria = vitoria
        )
    }

    private fun calculateNewGdp(
        econ: EconomicMetrics,
        budget: BudgetMetrics,
        gastoPrimarioMensal: Double,
        tau: Double,
        distLaffer: Double
    ): Double {
        // Multiplicador Keynesiano condicionado à Dívida/PIB
        // Se Divida/PIB < 60%: Mk = 1.2
        // Se 60% < Divida <= 80%: Mk vai de 1.2 a 0.2
        // Se Divida > 80%: Mk = -0.3 (Crowding Out)
        val dividaRatio = econ.dividaBrutaPctPib
        val multiplicador = when {
            dividaRatio <= 0.60 -> 1.2
            dividaRatio <= 0.80 -> 1.2 - 1.0 * ((dividaRatio - 0.60) / 0.20)
            else -> -0.30 // Efeito contracionista por juros e desconfiança fiscal
        }

        val taxaCrescimentoMensalBase = (econ.crescimentoPibProjAnual / 12.0)
        val impactoGasto = (multiplicador * 0.0004)
        val desincentivoTributario = if (distLaffer > 0.02) -(distLaffer * 0.002) else 0.0

        val taxaFinalMensal = (taxaCrescimentoMensalBase + impactoGasto + desincentivoTributario).coerceIn(-0.015, 0.015)
        return econ.pibNominalBrl * (1.0 + taxaFinalMensal)
    }

    private fun checkForEvents(
        mes: Int,
        cambio: Double,
        inflacao: Double,
        desemprego: Double,
        ministries: List<Ministry>,
        enterprises: List<StateEnterprise>
    ): GameEvent? {
        // Gatilho 1: Greve dos Caminhoneiros se Câmbio alto ou inflação de combustível
        if (cambio > 6.00 && Random.nextDouble() < 0.40) {
            return GameEvent(
                id = "greve_caminhoneiros_$mes",
                titulo = "Ameaça de Paralisação Nacional dos Caminhoneiros",
                descricao = "Com a alta do dólar a R$ ${cambio.format(2)}, o preço do diesel na bomba disparou 22%. Entidades autônomas ameaçam trancar rodovias em todo o país.",
                severidade = EventSeverity.CRISE_GRAVE,
                mesOcorrencia = mes,
                opcoes = listOf(
                    EventOption(
                        texto = "Subsidiar Diesel na Petrobras (Paridade Interna)",
                        descricaoImpacto = "Reduz lucro da Petrobras em R$ 15 bi, evita greve e segura inflação.",
                        impactoCustoBrl = 15_000_000_000.0,
                        impactoAprovacaoC = 6.0,
                        impactoAprovacaoAB = -4.0,
                        impactoInflacaoBps = -0.004
                    ),
                    EventOption(
                        texto = "Zerar PIS/Cofins do Combustível pelo Tesouro",
                        descricaoImpacto = "Custo fiscal direto de R$ 18 bi, melhora popularidade sem interferir na Petrobras.",
                        impactoCustoBrl = 18_000_000_000.0,
                        impactoAprovacaoGeral = 5.0,
                        impactoCdsBps = 15.0
                    ),
                    EventOption(
                        texto = "Manter PPI de Mercado e Acionar Forças de Segurança",
                        descricaoImpacto = "Mantém responsabilidade fiscal, mas arrisca bloqueios e desabastecimento em supermercados.",
                        impactoAprovacaoGeral = -7.0,
                        impactoAprovacaoAB = 3.0,
                        impactoInflacaoBps = 0.008
                    )
                )
            )
        }

        // Gatilho 2: Desastre Ambiental / Queimadas
        val minMeioAmb = ministries.find { it.id == MinistryType.MEIO_AMBIENTE }
        if ((minMeioAmb?.orcamentoInvestimentoAnualBrl ?: 0.0) < 3_000_000_000.0 && mes % 6 == 0) {
            return GameEvent(
                id = "crise_ambiental_$mes",
                titulo = "Pressão Internacional e Queimadas no Pantanal e Amazônia",
                descricao = "Imagens de satélite mostram avanço do fogo. Países da União Europeia ameaçam suspender ratificação de acordos comerciais com o agronegócio.",
                severidade = EventSeverity.ALERTA,
                mesOcorrencia = mes,
                opcoes = listOf(
                    EventOption(
                        texto = "Decretar Operação de GLO com as Forças Armadas",
                        descricaoImpacto = "Gasto emergencial de R$ 2,5 bi, acalma parceiros externos e melhora imagem do Brasil.",
                        impactoCustoBrl = 2_500_000_000.0,
                        impactoAprovacaoAB = 4.0,
                        impactoAprovacaoGeral = 2.0
                    ),
                    EventOption(
                        texto = "Rejeitar 'Interferência Externa' em Soberania Nacional",
                        descricaoImpacto = "Apoio da bancada do agro, mas risco de sanções comerciais e fuga de fundos ESG.",
                        impactoApoioCongresso = 4.0,
                        impactoCdsBps = 25.0,
                        impactoAprovacaoAB = -3.0
                    )
                )
            )
        }

        // Evento Aleatório Periódico: Acordo de Investimento Internacional
        if (mes == 8 || mes == 20 || mes == 32) {
            return GameEvent(
                id = "cupula_brics_$mes",
                titulo = "Cúpula de Líderes Globais & Transição Energética",
                descricao = "Grandes potências oferecem pacote bilateral de cooperação para produção de hidrogênio verde e minerais críticos.",
                severidade = EventSeverity.OPORTUNIDADE,
                mesOcorrencia = mes,
                opcoes = listOf(
                    EventOption(
                        texto = "Fechar Parceria Estratégica Ampla",
                        descricaoImpacto = "Atrai USD 12 bilhões em investimentos privados, reduzindo o risco-país.",
                        impactoCdsBps = -35.0,
                        impactoAprovacaoGeral = 4.0,
                        impactoCambio = -0.15
                    ),
                    EventOption(
                        texto = "Priorizar Conteúdo Local Obrigatório",
                        descricaoImpacto = "Garante empregos na indústria nacional, mas atrasa implementação das obras.",
                        impactoAprovacaoC = 3.0,
                        impactoApoioCongresso = 2.0
                    )
                )
            )
        }

        return null
    }

    private fun getNomeMes(mes: Int): String {
        val meses = listOf(
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        )
        val index = (mes - 1) % 12
        return "${meses[index]} de ${2026 + (mes - 1) / 12}"
    }

    private fun Double.format(digits: Int): String = String.format(java.util.Locale.US, "%.${digits}f", this)
}
