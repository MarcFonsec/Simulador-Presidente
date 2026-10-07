package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.PresidentialViewModel
import com.example.ui.components.EventDialog
import com.example.ui.components.GameOverDialog
import com.example.ui.components.GitHubExportDialog
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PresidentialGreenLight
import com.example.ui.theme.SlateNavyCard
import com.example.ui.theme.SlateNavyDark

enum class MainTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    DASHBOARD("Visão Geral", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "tab_dashboard"),
    ECONOMY("Economia", Icons.Filled.TrendingUp, Icons.Outlined.TrendingUp, "tab_economy"),
    MINISTRIES("Ministérios", Icons.Filled.AccountBalance, Icons.Outlined.AccountBalance, "tab_ministries"),
    CONGRESS("Congresso", Icons.Filled.HowToVote, Icons.Outlined.HowToVote, "tab_congress"),
    ENTERPRISES("Estatais", Icons.Filled.Business, Icons.Outlined.Business, "tab_enterprises")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PresidentialApp()
            }
        }
    }
}

@Composable
fun PresidentialApp(
    viewModel: PresidentialViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(MainTab.DASHBOARD) }
    var showGitHubDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("presidential_app_root"),
        containerColor = SlateNavyDark,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                containerColor = SlateNavyCard,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .testTag("main_navigation_bar")
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                MainTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title
                            )
                        },
                        label = { Text(text = tab.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SlateNavyDark,
                            selectedTextColor = PresidentialGreenLight,
                            indicatorColor = PresidentialGreenLight
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                MainTab.DASHBOARD -> DashboardScreen(
                    state = state,
                    onAdvanceMonth = { viewModel.advanceMonth() },
                    onSelectDifficulty = { diff -> viewModel.setDifficulty(diff) },
                    onOpenGitHubExport = { showGitHubDialog = true }
                )
                MainTab.ECONOMY -> EconomyScreen(
                    state = state,
                    onUpdateTax = { tipo, taxa -> viewModel.updateTaxRate(tipo, taxa) }
                )
                MainTab.MINISTRIES -> MinistriesScreen(
                    state = state,
                    onToggleProfile = { id -> viewModel.toggleMinisterProfile(id) },
                    onUpdateBudget = { id, cust, inv -> viewModel.updateMinistryBudget(id, cust, inv) }
                )
                MainTab.CONGRESS -> CongressScreen(
                    state = state,
                    onLiberarEmendas = { valor -> viewModel.updateParliamentaryAmendments(valor) },
                    onSubmitBill = { id -> viewModel.submitBillToCongress(id) }
                )
                MainTab.ENTERPRISES -> EnterprisesScreen(
                    state = state,
                    onSetPricePolicy = { id, pol -> viewModel.setEnterprisePricePolicy(id, pol) },
                    onRestructureEnterprise = { id -> viewModel.privatizeOrRestructureEnterprise(id) }
                )
            }

            // Dialog de Exportação para o GitHub
            if (showGitHubDialog) {
                GitHubExportDialog(
                    onDismiss = { showGitHubDialog = false }
                )
            }

            // Dialog de Crises ou Decisões Ativas
            if (state.eventoAtivo != null) {
                EventDialog(
                    event = state.eventoAtivo!!,
                    onSelectOption = { index -> viewModel.resolveActiveEvent(index) }
                )
            }

            // Dialog de Fim de Jogo (Vitória ou Derrota)
            if (state.fimDeJogo) {
                GameOverDialog(
                    state = state,
                    onRestartWithDifficulty = { diff -> viewModel.restartSimulation(diff) }
                )
            }
        }
    }
}
