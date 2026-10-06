package com.example.ui

import androidx.lifecycle.ViewModel
import com.example.data.model.*
import com.example.domain.InitialStateProvider
import com.example.domain.SimulationEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PresidentialViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(InitialStateProvider.createInitialState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    fun advanceMonth() {
        _uiState.update { current ->
            SimulationEngine.advanceMonth(current)
        }
    }

    fun restartSimulation() {
        _uiState.value = InitialStateProvider.createInitialState()
    }

    fun updateTaxRate(impostoTipo: String, novaAliquota: Double) {
        _uiState.update { current ->
            val econ = current.economicMetrics
            val newEcon = when (impostoTipo) {
                "IR" -> econ.copy(aliquotaImpostoRenda = novaAliquota.coerceIn(0.10, 0.45))
                "CONSUMO" -> econ.copy(aliquotaConsumoIva = novaAliquota.coerceIn(0.15, 0.38))
                "EMPRESAS" -> econ.copy(aliquotaEmpresas = novaAliquota.coerceIn(0.15, 0.50))
                else -> econ
            }
            current.copy(economicMetrics = newEcon)
        }
    }

    fun updateMinistryBudget(id: MinistryType, deltaCusteioPct: Double, deltaInvestimentoPct: Double) {
        _uiState.update { current ->
            val updatedMinistries = current.ministries.map { m ->
                if (m.id == id) {
                    val novoCusteio = (m.orcamentoCusteioAnualBrl * (1.0 + deltaCusteioPct)).coerceAtLeast(1_000_000_000.0)
                    val novoInvest = (m.orcamentoInvestimentoAnualBrl * (1.0 + deltaInvestimentoPct)).coerceAtLeast(500_000_000.0)
                    // Eficiência responde aos investimentos
                    val novaEficiencia = (m.eficienciaSetorialPct + deltaInvestimentoPct * 15.0).coerceIn(30.0, 98.0)
                    m.copy(
                        orcamentoCusteioAnualBrl = novoCusteio,
                        orcamentoInvestimentoAnualBrl = novoInvest,
                        eficienciaSetorialPct = novaEficiencia
                    )
                } else m
            }
            current.copy(ministries = updatedMinistries)
        }
    }

    fun toggleMinisterProfile(id: MinistryType) {
        _uiState.update { current ->
            val updatedMinistries = current.ministries.map { m ->
                if (m.id == id) {
                    val novoPerfil = if (m.perfil == MinisterProfile.TECNICO) MinisterProfile.POLITICO else MinisterProfile.TECNICO
                    val novaEficiencia = if (novoPerfil == MinisterProfile.TECNICO) {
                        (m.eficienciaSetorialPct + 8.0).coerceAtMost(98.0)
                    } else {
                        (m.eficienciaSetorialPct - 8.0).coerceAtLeast(40.0)
                    }
                    m.copy(perfil = novoPerfil, eficienciaSetorialPct = novaEficiencia)
                } else m
            }
            current.copy(ministries = updatedMinistries)
        }
    }

    fun updateParliamentaryAmendments(novoValorMensalBrl: Double) {
        _uiState.update { current ->
            val pol = current.politicalMetrics.copy(
                emendasLiberadasMesBrl = novoValorMensalBrl.coerceIn(1_000_000_000.0, 10_000_000_000.0)
            )
            current.copy(politicalMetrics = pol)
        }
    }

    fun setEnterprisePricePolicy(enterpriseId: String, policy: PricePolicy) {
        _uiState.update { current ->
            val updatedEnterprises = current.enterprises.map { ent ->
                if (ent.id == enterpriseId) {
                    ent.copy(politicaPreco = policy)
                } else ent
            }
            current.copy(enterprises = updatedEnterprises)
        }
    }

    fun privatizeOrRestructureEnterprise(enterpriseId: String) {
        _uiState.update { current ->
            val updatedEnterprises = current.enterprises.map { ent ->
                if (ent.id == enterpriseId) {
                    ent.copy(saneada = true, privatizada = true)
                } else ent
            }
            current.copy(enterprises = updatedEnterprises)
        }
    }

    fun submitBillToCongress(billId: String) {
        _uiState.update { current ->
            val bill = current.bills.find { it.id == billId } ?: return@update current
            if (bill.status != BillStatus.NA_GAVETA) return@update current

            val capitalNecessario = bill.impactoCapitalPoliticoNecessario
            if (current.politicalMetrics.capitalPolitico < capitalNecessario) {
                // Capital político insuficiente
                val news = listOf("Presidência: Capital Político insuficiente para articular aprovação da ${bill.titulo}.") + current.noticiasRecentes
                return@update current.copy(noticiasRecentes = news)
            }

            // Cálculo dos votos no plenário
            // Votos = Base + (Centrão * taxa de apoio)
            val apoioCentraoPct = (current.politicalMetrics.apoioCongressoPct / 100.0)
            val votosCentrao = (current.politicalMetrics.deputadosCentrao * apoioCentraoPct).toInt()
            val votosTotais = current.politicalMetrics.deputadosBase + votosCentrao

            val aprovada = votosTotais >= bill.votosNecessarios
            val novoStatus = if (aprovada) BillStatus.APROVADA else BillStatus.REJEITADA

            val logResultado = if (aprovada) {
                "✅ CONGRESSO APROVA: '${bill.titulo}' obteve $votosTotais votos favoráveis (necessários: ${bill.votosNecessarios})."
            } else {
                "❌ CONGRESSO REJEITA: '${bill.titulo}' obteve apenas $votosTotais votos (necessários: ${bill.votosNecessarios}). Derrota do governo!"
            }

            var novoEcon = current.economicMetrics
            var novoBudget = current.budgetMetrics
            var novoSocial = current.socialMetrics

            if (aprovada) {
                if (bill.impactoFiscalAnualBrl > 0) {
                    // Economia fiscal
                    novoEcon = novoEcon.copy(
                        resultadoPrimarioAnualBrl = novoEcon.resultadoPrimarioAnualBrl + bill.impactoFiscalAnualBrl,
                        custoRiscoPaisCdsBps = (novoEcon.custoRiscoPaisCdsBps - 15.0).coerceAtLeast(100.0)
                    )
                } else {
                    // Aumento de gasto / renúncia fiscal
                    novoEcon = novoEcon.copy(
                        resultadoPrimarioAnualBrl = novoEcon.resultadoPrimarioAnualBrl + bill.impactoFiscalAnualBrl
                    )
                }
                novoSocial = novoSocial.copy(
                    aprovacaoPopularGeralPct = (novoSocial.aprovacaoPopularGeralPct + bill.impactoAprovacaoGeral).coerceIn(5.0, 95.0)
                )
            }

            val novoPol = current.politicalMetrics.copy(
                capitalPolitico = (current.politicalMetrics.capitalPolitico - capitalNecessario).coerceAtLeast(0)
            )

            val updatedBills = current.bills.map {
                if (it.id == billId) it.copy(status = novoStatus, mesVotacao = current.mesAtual) else it
            }

            current.copy(
                bills = updatedBills,
                politicalMetrics = novoPol,
                economicMetrics = novoEcon,
                budgetMetrics = novoBudget,
                socialMetrics = novoSocial,
                noticiasRecentes = listOf(logResultado) + current.noticiasRecentes
            )
        }
    }

    fun resolveActiveEvent(optionIndex: Int) {
        _uiState.update { current ->
            val event = current.eventoAtivo ?: return@update current
            if (optionIndex !in event.opcoes.indices) return@update current

            val option = event.opcoes[optionIndex]
            var econ = current.economicMetrics
            var social = current.socialMetrics
            var pol = current.politicalMetrics

            // Aplicar impactos da opção escolhida
            if (option.impactoCustoBrl > 0) {
                econ = econ.copy(
                    resultadoPrimarioAnualBrl = econ.resultadoPrimarioAnualBrl - option.impactoCustoBrl
                )
            }
            if (option.impactoAprovacaoGeral != 0.0) {
                social = social.copy(
                    aprovacaoPopularGeralPct = (social.aprovacaoPopularGeralPct + option.impactoAprovacaoGeral).coerceIn(5.0, 95.0)
                )
            }
            if (option.impactoAprovacaoAB != 0.0) {
                social = social.copy(
                    aprovacaoClasseABPct = (social.aprovacaoClasseABPct + option.impactoAprovacaoAB).coerceIn(5.0, 95.0)
                )
            }
            if (option.impactoAprovacaoC != 0.0) {
                social = social.copy(
                    aprovacaoClasseCPct = (social.aprovacaoClasseCPct + option.impactoAprovacaoC).coerceIn(5.0, 95.0)
                )
            }
            if (option.impactoApoioCongresso != 0.0) {
                pol = pol.copy(
                    apoioCongressoPct = (pol.apoioCongressoPct + option.impactoApoioCongresso).coerceIn(10.0, 90.0)
                )
            }
            if (option.impactoCdsBps != 0.0) {
                econ = econ.copy(
                    custoRiscoPaisCdsBps = (econ.custoRiscoPaisCdsBps + option.impactoCdsBps).coerceIn(100.0, 850.0)
                )
            }
            if (option.impactoCambio != 0.0) {
                econ = econ.copy(
                    taxaCambioUsdBrl = (econ.taxaCambioUsdBrl + option.impactoCambio).coerceIn(4.50, 8.00)
                )
            }

            val news = listOf("Decisão Presidencial sobre '${event.titulo}': Escolhido '${option.texto}'.") + current.noticiasRecentes

            current.copy(
                economicMetrics = econ,
                socialMetrics = social,
                politicalMetrics = pol,
                eventoAtivo = null,
                noticiasRecentes = news
            )
        }
    }
}
