package com.example.domain

import com.example.data.model.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

object SimulationEngine {

    fun advanceMonth(currentState: GameState): GameState {
        if (currentState.fimDeJogo) return currentState

        val diff = currentState.dificuldade
        val novoMes = currentState.mesAtual + 1
        val novoAno = 2026 + (novoMes - 1) / 12

        var econ = currentState.economicMetrics
        var budget = currentState.budgetMetrics
        var social = currentState.socialMetrics
        var pol = currentState.politicalMetrics
        var ministries = currentState.ministries
        var enterprises = currentState.enterprises
        val logsNoticias = mutableListOf<String>()
        val fatoresPopularidade = mutableListOf<PopularityFactor>()

        // 1. CÁLCULO DA ARRECADAÇÃO (Curva de Laffer Suavizada)
        // Ponto ideal tau* = 34.0%
        val tau = (econ.aliquotaImpostoRenda * 0.35 + econ.aliquotaConsumoIva * 0.40 + econ.aliquotaEmpresas * 0.25)
        val tauIdeal = 0.34
        val distLaffer = tau - tauIdeal
        // Fator de eficiência de Laffer com penalidade modulada pela dificuldade
        val lafferPenaltyExponent = (1.4 * diff.penaltyMultiplier)
        val lafferFactor = (1.0 - lafferPenaltyExponent * distLaffer.pow(2)).coerceIn(0.75, 1.08)
        
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

        // 2. TEMPORALIDADE ACELERADA DOS INVESTIMENTOS DOS MINISTÉRIOS (3 A 6 MESES)
        ministries = ministries.map { m ->
            val investRatio = m.orcamentoInvestimentoAnualBrl / 10_000_000_000.0
            // Eficiência converge mais rapidamente para a meta de investimento
            val metaEficiencia = (60.0 + investRatio * 5.0 + if (m.perfil == MinisterProfile.TECNICO) 10.0 else 0.0).coerceIn(40.0, 98.0)
            val novaEficiencia = (m.eficienciaSetorialPct + (metaEficiencia - m.eficienciaSetorialPct) * 0.25).coerceIn(35.0, 98.0)
            val novaSatisfacao = (m.satisfacaoCategoriaPct + (m.orcamentoCusteioAnualBrl / 100_000_000_000.0 - 0.5) * 2.0).coerceIn(30.0, 98.0)
            m.copy(eficienciaSetorialPct = novaEficiencia, satisfacaoCategoriaPct = novaSatisfacao)
        }

        // 3. GASTOS DO MÊS
        val gastosObrigMensais = (budget.gastosObrigatorios.totalBrl / 12.0) + custoMensalDependentes
        val investimentosMinisteriosMensal = ministries.sumOf { it.orcamentoInvestimentoAnualBrl / 12.0 }
        val custeioMinisteriosMensal = ministries.sumOf { it.orcamentoCusteioAnualBrl / 12.0 }
        val gastosDiscrecMensais = investimentosMinisteriosMensal + custeioMinisteriosMensal
        val emendasMensais = pol.emendasLiberadasMesBrl

        val gastoPrimarioMensal = gastosObrigMensais + gastosDiscrecMensais + emendasMensais

        // 4. RESULTADO PRIMÁRIO E NOMINAL
        val resultadoPrimarioMensal = arrecadacaoMensalBase - gastoPrimarioMensal
        val resultadoPrimarioAnualProjetado = resultadoPrimarioMensal * 12.0

        // Juros da Dívida e impacto da Selic
        val custoJurosMensal = (econ.dividaBrutaBrl * (econ.taxaSelicAnual * 0.68 + 0.035 * 0.32)) / 12.0
        val resultadoNominalMensal = resultadoPrimarioMensal - custoJurosMensal
        val resultadoNominalAnualProjetado = resultadoNominalMensal * 12.0

        // Dívida Bruta atualizada
        val novaDividaBruta = econ.dividaBrutaBrl - resultadoNominalMensal
        val novoPib = calculateNewGdp(econ, budget, gastoPrimarioMensal, tau, distLaffer, diff)
        val novaDividaPctPib = (novaDividaBruta / novoPib).coerceAtLeast(0.40)

        // 5. RISCO-PAÍS (CDS 5 ANOS) & RATING COM RETORNO DE INVESTIMENTOS
        val baseCds = 205.0
        val fatorDivida = max(0.0, (novaDividaPctPib - 0.78) * 600.0 * diff.penaltyMultiplier)
        val fatorDeficit = max(0.0, (-resultadoNominalAnualProjetado - 650_000_000_000.0) / 15_000_000_000.0) * diff.penaltyMultiplier
        
        // Retorno de relações exteriores atrai investimentos e reduz CDS
        val minMre = ministries.find { it.id == MinistryType.RELACOES_EXTERIORES }
        val bonusMre = ((minMre?.eficienciaSetorialPct ?: 75.0) - 75.0) * 1.5

        val novoCds = (baseCds + fatorDivida + fatorDeficit - bonusMre).coerceIn(110.0, 800.0)

        val novoRating = when {
            novoCds < 170 -> "BBB- (Grau de Investimento)"
            novoCds < 240 -> "BB- (Estável)"
            novoCds < 350 -> "B+ (Risco Moderado)"
            novoCds < 550 -> "CCC (Alerta de Degradação)"
            else -> "D (Risco Iminente de Calote/Default)"
        }

        // 6. CÂMBIO (USD / BRL) & INFLAÇÃO (IPCA) & CURVA DE PHILLIPS SUAVIZADA
        val taxaCambioBase = 5.35
        val variacaoCds = (novoCds - 205.0) / 100.0 * 0.22
        val novoCambio = (taxaCambioBase + variacaoCds).coerceIn(4.50, 7.20)

        // Curva de Phillips suavizada
        val desemprego = budget.taxaDesemprego
        val pressaoPhillips = max(0.0, (0.052 - desemprego) * 0.20 * diff.penaltyMultiplier)
        val passThroughCambial = max(0.0, (novoCambio - 5.35) * 0.005 * diff.penaltyMultiplier)
        
        // Custo logístico da infraestrutura com efeito rápido (3-6 meses)
        val minInfra = ministries.find { it.id == MinistryType.INFRAESTRUTURA }
        val efInfra = minInfra?.eficienciaSetorialPct ?: 70.0
        val freteInflacao = if (efInfra >= 75.0) -0.0035 else if (efInfra < 60.0) 0.003 else 0.0

        val novaInflacaoIpca = (econ.inflacaoIpca12m * 0.85 + (econ.metaInflacaoBc + pressaoPhillips + passThroughCambial + freteInflacao) * 0.15)
            .coerceIn(0.022, 0.20)

        // 7. BANCO CENTRAL & TAXA SELIC (Ancoragem mais responsiva)
        val metaInflacao = econ.metaInflacaoBc
        var novaSelic = econ.taxaSelicAnual
        if (novaInflacaoIpca <= metaInflacao + 0.010 && novaSelic > 0.095) {
            // Inflação ancorada: BC corta juros para estimular economia
            if (novoMes % 2 == 0) {
                novaSelic = max(0.085, novaSelic - 0.005)
                logsNoticias.add("Banco Central (Copom): Redução da taxa Selic para ${(novaSelic * 100).format(2)}% com inflação controlada.")
            }
        } else if (novaInflacaoIpca > metaInflacao + 0.025) {
            // Pressão inflacionária forte
            if (novoMes % 2 == 0) {
                novaSelic = min(0.20, novaSelic + 0.0025 * diff.penaltyMultiplier)
                logsNoticias.add("Banco Central (Copom): Elevação moderada da Selic para ${(novaSelic * 100).format(2)}% para ancorar IPCA.")
            }
        }

        // 8. MERCADO DE TRABALHO & RENDA
        val crescimentoTrimestre = (novoPib / econ.pibNominalBrl - 1.0)
        val novoDesemprego = (desemprego - (crescimentoTrimestre * 0.4) + (novaSelic - 0.12) * 0.01)
            .coerceIn(0.035, 0.12)
        val novaCestaBasica = (budget.salarioMinimoBrl * 0.48 * (1.0 + novaInflacaoIpca)).coerceAtLeast(650.0)

        // 9. REBALANCEAMENTO DA POPULARIDADE & DIAGNÓSTICO DE FATORES

        // Fator 1: Inflação e Custo de Vida
        val inflacaoImpacto = if (novaInflacaoIpca <= 0.038) {
            val bonus = 7.5 + (0.038 - novaInflacaoIpca) * 120.0
            fatoresPopularidade.add(PopularityFactor("Inflação Baixa & Estável (IPCA ${(novaInflacaoIpca * 100).format(1)}%)", bonus, "Poder de compra preservado nas compras do mês.", "Inflação"))
            bonus
        } else if (novaInflacaoIpca <= 0.048) {
            val bonus = 4.0
            fatoresPopularidade.add(PopularityFactor("Inflação sob Controle (IPCA ${(novaInflacaoIpca * 100).format(1)}%)", bonus, "Preços dentro do intervalo de tolerância do Banco Central.", "Inflação"))
            bonus
        } else {
            val penalty = -((novaInflacaoIpca - 0.048) * 140.0 * diff.penaltyMultiplier)
            fatoresPopularidade.add(PopularityFactor("Pressão Inflacionária (IPCA ${(novaInflacaoIpca * 100).format(1)}%)", penalty, "Aumento de alimentos e energia corrói salários.", "Inflação"))
            penalty
        }

        // Fator 2: Emprego e Renda
        val empregoImpacto = if (novoDesemprego <= 0.050) {
            val bonus = 7.0 + (0.050 - novoDesemprego) * 150.0
            fatoresPopularidade.add(PopularityFactor("Desemprego em Mínima Histórica (${(novoDesemprego * 100).format(1)}%)", bonus, "Geração expressiva de carteiras assinadas e renda formal.", "Emprego"))
            bonus
        } else if (novoDesemprego <= 0.065) {
            val bonus = 4.2
            fatoresPopularidade.add(PopularityFactor("Mercado de Trabalho Estável (${(novoDesemprego * 100).format(1)}%)", bonus, "Ocupação em níveis saudáveis para a economia.", "Emprego"))
            bonus
        } else {
            val penalty = -((novoDesemprego - 0.065) * 120.0 * diff.penaltyMultiplier)
            fatoresPopularidade.add(PopularityFactor("Desemprego Elevado (${(novoDesemprego * 100).format(1)}%)", penalty, "Dificuldade de inserção no mercado de trabalho formal.", "Emprego"))
            penalty
        }

        // Fator 3: Tributação sobre Consumo e Renda
        val tributoConsumoImpacto = if (econ.aliquotaConsumoIva < 0.260) {
            val bonus = 6.5 + (0.260 - econ.aliquotaConsumoIva) * 60.0
            fatoresPopularidade.add(PopularityFactor("Alívio Tributário no Consumo (${(econ.aliquotaConsumoIva * 100).format(1)}%)", bonus, "Produtos mais baratos para a Classe C e D/E.", "Tributação"))
            bonus
        } else if (econ.aliquotaConsumoIva > 0.280) {
            val penalty = -((econ.aliquotaConsumoIva - 0.280) * 50.0 * diff.penaltyMultiplier)
            fatoresPopularidade.add(PopularityFactor("Imposto Alto sobre Consumo (${(econ.aliquotaConsumoIva * 100).format(1)}%)", penalty, "Encargo elevado embutido no preço das mercadorias.", "Tributação"))
            penalty
        } else {
            0.0
        }

        // Fator 4: Política Social & Piso Salarial (Bolsa Família, Saúde, Educação)
        val minSocial = ministries.find { it.id == MinistryType.DESENVOLVIMENTO_SOCIAL }
        val minSaude = ministries.find { it.id == MinistryType.SAUDE }
        val minEduc = ministries.find { it.id == MinistryType.EDUCACAO }
        val efSocialMedia = ((minSocial?.eficienciaSetorialPct ?: 75.0) + (minSaude?.eficienciaSetorialPct ?: 75.0) + (minEduc?.eficienciaSetorialPct ?: 75.0)) / 3.0

        val servicosImpacto = if (efSocialMedia >= 78.0) {
            val bonus = 6.0 + (efSocialMedia - 78.0) * 0.4
            fatoresPopularidade.add(PopularityFactor("SUS, Educação e Bolsa Família Fortalecidos", bonus, "Serviços públicos com alta avaliação popular.", "Social"))
            bonus
        } else if (efSocialMedia < 60.0) {
            val penalty = -((60.0 - efSocialMedia) * 0.5 * diff.penaltyMultiplier)
            fatoresPopularidade.add(PopularityFactor("Filas no SUS e Queda na Assistência", penalty, "Descontentamento com serviços sociais básicos.", "Social"))
            penalty
        } else {
            val bonus = 2.5
            fatoresPopularidade.add(PopularityFactor("Assistência Social Operando com Regularidade", bonus, "Pagamentos pontuais e atendimento padrão.", "Social"))
            bonus
        }

        // Fator 5: Juros Selic e Crédito
        val jurosImpacto = if (novaSelic <= 0.11) {
            val bonus = 4.5
            fatoresPopularidade.add(PopularityFactor("Crédito e Financiamento Acessíveis (Selic ${(novaSelic * 100).format(1)}%)", bonus, "Aumento de compras a prazo e crédito imobiliário.", "Monetário"))
            bonus
        } else if (novaSelic >= 0.14) {
            val penalty = -((novaSelic - 0.13) * 60.0 * diff.penaltyMultiplier)
            fatoresPopularidade.add(PopularityFactor("Juros Selic Elevados (${(novaSelic * 100).format(1)}%)", penalty, "Crédito caro e juros de cartão pesando no bolso.", "Monetário"))
            penalty
        } else {
            0.0
        }

        // Fator 6: Responsabilidade Fiscal & Sustentabilidade da Dívida
        val fiscalImpacto = if (resultadoPrimarioAnualProjetado >= 0) {
            val bonus = 5.5
            fatoresPopularidade.add(PopularityFactor("Superávit Primário & Contas no Azul", bonus, "Confiança máxima do setor produtivo e Classe A/B.", "Fiscal"))
            bonus
        } else if (novaDividaPctPib > 0.86) {
            val penalty = -((novaDividaPctPib - 0.85) * 45.0 * diff.penaltyMultiplier)
            fatoresPopularidade.add(PopularityFactor("Dívida Pública Elevada (${(novaDividaPctPib * 100).format(1)}% do PIB)", penalty, "Insegurança quanto ao equilíbrio fiscal de longo prazo.", "Fiscal"))
            penalty
        } else {
            0.0
        }

        // Fator 7: Governabilidade e Apoio Parlamentar
        val percentualMinistrosPoliticos = ministries.count { it.perfil == MinisterProfile.POLITICO }.toDouble() / ministries.size.toDouble()
        val emendasFator = (pol.emendasLiberadasMesBrl / 4_330_000_000.0).coerceIn(0.5, 2.0)
        
        val apoioCentraoPct = (50.0 + (emendasFator - 1.0) * 28.0 + (percentualMinistrosPoliticos - 0.5) * 30.0)
            .coerceIn(15.0, 95.0)
        val deputadosCentraoApoiando = (pol.deputadosCentrao * (apoioCentraoPct / 100.0)).toInt()
        val totalApoioDeputados = pol.deputadosBase + deputadosCentraoApoiando
        val novoApoioCongressoPct = ((totalApoioDeputados.toDouble() / pol.totalDeputados.toDouble()) * 100.0).coerceIn(25.0, 95.0)

        val congressoImpacto = if (novoApoioCongressoPct >= 60.0) {
            val bonus = 3.5
            fatoresPopularidade.add(PopularityFactor("Ampla Maioria no Congresso (${novoApoioCongressoPct.toInt()}%)", bonus, "Estabilidade política e facilidade para aprovar projetos.", "Congresso"))
            bonus
        } else if (novoApoioCongressoPct < 38.0) {
            val penalty = -((38.0 - novoApoioCongressoPct) * 0.25 * diff.penaltyMultiplier)
            fatoresPopularidade.add(PopularityFactor("Congresso em Atrito com o Planalto", penalty, "Risco de travas em votações e CPIs.", "Congresso"))
            penalty
        } else {
            0.0
        }

        // Cálculo da Aprovação Alvo Bruta
        val baseScore = 50.0 + diff.approvalBonus
        val somaImpactos = inflacaoImpacto + empregoImpacto + tributoConsumoImpacto + servicosImpacto + jurosImpacto + fiscalImpacto + congressoImpacto

        val aprovacaoAlvoCalculada = (baseScore + somaImpactos).coerceIn(15.0, 95.0)

        // Aplicação do Piso Máximo de Variação Mensal (não despenque mais de X% por ciclo)
        val aprovacaoAnterior = social.aprovacaoPopularGeralPct
        val deltaBruto = aprovacaoAlvoCalculada - aprovacaoAnterior
        val deltaLimitado = if (deltaBruto < 0) {
            max(-diff.maxMonthlyDrop, deltaBruto) // Piso: não cai mais do que diff.maxMonthlyDrop (ex: -5%)
        } else {
            min(8.0, deltaBruto) // Subida suave de até +8% ao mês
        }

        val novaAprovacaoGeral = (aprovacaoAnterior + deltaLimitado).coerceIn(10.0, 95.0)

        // Aprovação por Classes Sociais
        val scoreAB = (novaAprovacaoGeral - 5.0 + fiscalImpacto * 0.8 + inflacaoImpacto * 0.4).coerceIn(10.0, 95.0)
        val scoreC = (novaAprovacaoGeral + empregoImpacto * 0.5 + tributoConsumoImpacto * 0.6).coerceIn(10.0, 95.0)
        val scoreDE = (novaAprovacaoGeral + 4.0 + servicosImpacto * 0.6 + tributoConsumoImpacto * 0.5).coerceIn(10.0, 95.0)

        // Risco de Impeachment (com alta tolerância se mantiver base ou aprovação)
        var riscoImpeachment = pol.riscoImpeachmentPct
        if (novaAprovacaoGeral < 20.0 && novoApoioCongressoPct < 30.0) {
            riscoImpeachment = min(100.0, riscoImpeachment + 8.0 * diff.penaltyMultiplier)
            logsNoticias.add("⚠️ ALERTA EM BRASÍLIA: Oposição articula pedido de Impeachment devido a crise de apoio e popularidade!")
        } else if (novaAprovacaoGeral > 35.0 || novoApoioCongressoPct > 48.0) {
            riscoImpeachment = max(0.0, riscoImpeachment - 8.0)
        }

        // Risco de Defesa
        val minDefesa = ministries.find { it.id == MinistryType.DEFESA }
        val lealdadeMilitar = minDefesa?.satisfacaoCategoriaPct ?: 85.0

        // Geração de Evento Aleatório ou Disparado por Gatilho
        val evento = checkForEvents(novoMes, novoCambio, novaInflacaoIpca, novoDesemprego, ministries, enterprises)

        // Checagem de Fim de Jogo
        var fimDeJogo = false
        var motivoFim: String? = null
        var vitoria = false

        if (riscoImpeachment >= 95.0) {
            fimDeJogo = true
            motivoFim = "IMPEACHMENT: Aprovado pelo Congresso Nacional por perda total de base e colapso na aprovação popular."
        } else if (lealdadeMilitar < 15.0) {
            fimDeJogo = true
            motivoFim = "RUPTURA INSTITUCIONAL: Desestabilização grave nas Forças Armadas após sucateamento da Defesa."
        } else if (novaDividaPctPib > 1.15 && novoCds > 750.0) {
            fimDeJogo = true
            motivoFim = "DEFAULT & MORATÓRIA: Calote na dívida pública com fuga de capitais descontrolada."
        } else if (novoMes >= 48) {
            fimDeJogo = true
            vitoria = true
            motivoFim = "FIM DO MANDATO (48 Meses): Você concluiu com sucesso os 4 anos de governo republicano! O país garantiu desenvolvimento econômico e estabilidade democrática."
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
            aprovacaoPct = novaAprovacaoGeral,
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
                aprovacaoPopularGeralPct = novaAprovacaoGeral,
                aprovacaoClasseABPct = scoreAB,
                aprovacaoClasseCPct = scoreC,
                aprovacaoClasseDEPct = scoreDE,
                variacaoUltimoMesPct = deltaLimitado,
                custoCestaBasicaBrl = novaCestaBasica,
                fatoresPopularidade = fatoresPopularidade
            ),
            politicalMetrics = pol.copy(
                apoioCongressoPct = novoApoioCongressoPct,
                riscoImpeachmentPct = riscoImpeachment,
                capitalPolitico = (pol.capitalPolitico + 3).coerceAtMost(100)
            ),
            ministries = ministries,
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
        distLaffer: Double,
        diff: GameDifficulty
    ): Double {
        val dividaRatio = econ.dividaBrutaPctPib
        val multiplicadorBase = when {
            dividaRatio <= 0.65 -> 1.3
            dividaRatio <= 0.85 -> 1.3 - 1.1 * ((dividaRatio - 0.65) / 0.20)
            else -> -0.15 * diff.penaltyMultiplier
        }
        val multiplicador = multiplicadorBase + diff.keynesianMultiplierBoost

        val taxaCrescimentoMensalBase = (econ.crescimentoPibProjAnual / 12.0)
        val impactoGasto = (multiplicador * 0.0004)
        val desincentivoTributario = if (distLaffer > 0.03) -(distLaffer * 0.0015 * diff.penaltyMultiplier) else 0.0

        val taxaFinalMensal = (taxaCrescimentoMensalBase + impactoGasto + desincentivoTributario).coerceIn(-0.010, 0.018)
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
        if (cambio > 6.40 && Random.nextDouble() < 0.25) {
            return GameEvent(
                id = "greve_caminhoneiros_$mes",
                titulo = "Ameaça de Paralisação Nacional dos Caminhoneiros",
                descricao = "Com a alta do dólar a R$ ${cambio.format(2)}, o preço do diesel na bomba subiu. Entidades autônomas dialogam com o governo federal.",
                severidade = EventSeverity.CRISE_GRAVE,
                mesOcorrencia = mes,
                opcoes = listOf(
                    EventOption(
                        texto = "Amortecer Preço na Petrobras (Paridade Interna)",
                        descricaoImpacto = "Segura o preço do frete e gera +6% de aprovação popular.",
                        impactoCustoBrl = 8_000_000_000.0,
                        impactoAprovacaoC = 6.0,
                        impactoAprovacaoGeral = 5.0,
                        impactoInflacaoBps = -0.003
                    ),
                    EventOption(
                        texto = "Reduzir PIS/Cofins do Combustível pelo Tesouro",
                        descricaoImpacto = "Custo fiscal direto moderado, preservando a governança da Petrobras.",
                        impactoCustoBrl = 10_000_000_000.0,
                        impactoAprovacaoGeral = 6.0
                    ),
                    EventOption(
                        texto = "Manter PPI de Mercado com Linha de Crédito para Frotas",
                        descricaoImpacto = "Mantém responsabilidade fiscal e oferece financiamento do BNDES.",
                        impactoAprovacaoAB = 4.0,
                        impactoAprovacaoGeral = 1.0
                    )
                )
            )
        }

        if (mes == 6 || mes == 18 || mes == 30) {
            return GameEvent(
                id = "acordo_comercial_$mes",
                titulo = "Cúpula de Investimento Internacional & Transição Verde",
                descricao = "Parceiros internacionais propõem pacote bilateral para atração de capitais e projetos de energia limpa no Brasil.",
                severidade = EventSeverity.OPORTUNIDADE,
                mesOcorrencia = mes,
                opcoes = listOf(
                    EventOption(
                        texto = "Ratificar Parceria Ampla de Investimento",
                        descricaoImpacto = "Atrai USD 15 bilhões em IDE, reduz o risco-país e gera empregos.",
                        impactoCdsBps = -30.0,
                        impactoAprovacaoGeral = 5.5,
                        impactoCambio = -0.12
                    ),
                    EventOption(
                        texto = "Exigir Parceria com Fornecedores Nacionais",
                        descricaoImpacto = "Impulsiona a indústria local com apoio de bancadas industriais.",
                        impactoAprovacaoC = 4.0,
                        impactoApoioCongresso = 3.0
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
