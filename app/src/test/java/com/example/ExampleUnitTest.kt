package com.example

import com.example.data.model.GameDifficulty
import com.example.domain.InitialStateProvider
import com.example.domain.SimulationEngine
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testInitialStateMatches2026BrazilBaseline() {
        val state = InitialStateProvider.createInitialState()

        assertEquals(1, state.mesAtual)
        assertEquals(2026, state.anoAtual)
        assertEquals(12_700_000_000_000.0, state.economicMetrics.pibNominalBrl, 1.0)
        assertEquals(0.1400, state.economicMetrics.taxaSelicAnual, 0.0001)
        assertEquals(0.829, state.economicMetrics.dividaBrutaPctPib, 0.001)
        assertEquals(5.42, state.economicMetrics.taxaCambioUsdBrl, 0.01)
        assertEquals(210.0, state.economicMetrics.custoRiscoPaisCdsBps, 0.1)
        assertEquals(9, state.ministries.size)
        assertEquals(9, state.enterprises.size)
        assertTrue(state.bills.isNotEmpty())
        assertTrue(state.socialMetrics.fatoresPopularidade.isNotEmpty())
    }

    @Test
    fun testSimulationEngineAdvancesMonthAndUpdatesMetrics() {
        val initial = InitialStateProvider.createInitialState()
        val next = SimulationEngine.advanceMonth(initial)

        assertEquals(2, next.mesAtual)
        assertEquals(2, next.history.size)
        assertTrue(next.economicMetrics.dividaBrutaBrl > 0)
        assertTrue(next.socialMetrics.aprovacaoPopularGeralPct in 0.0..100.0)
        assertTrue(next.politicalMetrics.apoioCongressoPct in 0.0..100.0)
        assertTrue(next.socialMetrics.fatoresPopularidade.isNotEmpty())
    }

    @Test
    fun testApprovalDropIsCapped() {
        val initial = InitialStateProvider.createInitialState(GameDifficulty.NORMAL)
        val next = SimulationEngine.advanceMonth(initial)
        val delta = next.socialMetrics.aprovacaoPopularGeralPct - initial.socialMetrics.aprovacaoPopularGeralPct
        
        // Deve respeitar o piso de variação de no máximo -5% ao mês
        assertTrue("Aprovação não pode cair mais de 5% em um único mês", delta >= -5.0)
    }

    @Test
    fun testEasyDifficultyProvidesApprovalBoost() {
        val easyState = InitialStateProvider.createInitialState(GameDifficulty.FACIL)
        val hardState = InitialStateProvider.createInitialState(GameDifficulty.REALISTA_HARDCORE)

        assertTrue(easyState.socialMetrics.aprovacaoPopularGeralPct > hardState.socialMetrics.aprovacaoPopularGeralPct)
    }

    @Test
    fun testMandatorySpendingIsDominant() {
        val state = InitialStateProvider.createInitialState()
        val ratio = state.budgetMetrics.percentualObrigatorio
        assertTrue("Gastos obrigatórios devem ser > 90%", ratio > 90.0)
    }
}
