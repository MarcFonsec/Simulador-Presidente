package com.example.domain

import com.example.data.model.*

object InitialStateProvider {

    fun createInitialState(): GameState {
        val initialEcon = EconomicMetrics(
            pibNominalBrl = 12_700_000_000_000.0,
            pibUsd = 2_640_000_000_000.0,
            pibPerCapitaBrl = 60_500.0,
            crescimentoPibProjAnual = 0.025,
            pibPotencialBrl = 12_850_000_000_000.0,
            taxaSelicAnual = 0.1400,
            inflacaoIpca12m = 0.0422,
            metaInflacaoBc = 0.0300,
            taxaCambioUsdBrl = 5.42,
            reservasInternacionaisUsd = 372_600_000_000.0,
            balancaComercialSaldoUsd = 75_000_000_000.0,
            dividaBrutaBrl = 11_100_000_000_000.0,
            dividaBrutaPctPib = 0.829,
            dividaPublicaFederalBrl = 9_290_000_000_000.0,
            resultadoPrimarioAnualBrl = -65_000_000_000.0,
            resultadoNominalAnualBrl = -820_000_000_000.0,
            gastoAnualJurosDividaBrl = 755_000_000_000.0,
            arrecadacaoFederalBrutaAnualBrl = 2_650_000_000_000.0,
            custoRiscoPaisCdsBps = 210.0,
            creditRating = "BB- (Perspectiva Estável)",
            aliquotaImpostoRenda = 0.275,
            aliquotaConsumoIva = 0.265,
            aliquotaEmpresas = 0.340,
            cargaTributariaEfetivaPct = 0.338
        )

        val initialBudget = BudgetMetrics(
            gastosObrigatorios = MandatorySpending(
                previdenciaSocialInssBrl = 1_120_000_000_000.0,
                folhaFuncionalismoBrl = 385_000_000_000.0,
                pisoSaudeBrl = 232_000_000_000.0,
                pisoEducacaoBrl = 182_000_000_000.0,
                transferenciaRendaBrl = 285_000_000_000.0,
                subsidiosSubvencoesBrl = 115_000_000_000.0
            ),
            gastosDiscrecionarios = DiscretionarySpending(
                infraestruturaInvestimentoBrl = 78_000_000_000.0,
                custeioOperacionalMinisteriosBrl = 92_000_000_000.0,
                defesaSegurancaInvestimentoBrl = 18_000_000_000.0
            ),
            emendasParlamentaresTotaisBrl = 52_000_000_000.0,
            salarioMinimoBrl = 1621.0,
            rendimentoMedioRealBrl = 3777.0,
            massaRendimentoMensalBrl = 385_600_000_000.0,
            taxaDesemprego = 0.053,
            taxaSubutilizacao = 0.131,
            taxaInformalidade = 0.385,
            populacaoOcupada = 103_477_000L
        )

        val initialSocial = SocialMetrics(
            aprovacaoPopularGeralPct = 48.0,
            aprovacaoClasseABPct = 42.0,
            aprovacaoClasseCPct = 49.0,
            aprovacaoClasseDEPct = 55.0,
            indiceGini = 0.518,
            idh = 0.760,
            custoCestaBasicaBrl = 815.0,
            familiasEndividadasPct = 71.4,
            familiasInadimplentesPct = 28.8,
            taxaHomicidiosPor100k = 18.2,
            riscoProtestoSocialPct = 15.0,
            populacaoTotal = 216_400_000L
        )

        val initialPol = PoliticalMetrics(
            apoioCongressoPct = 54.0,
            deputadosBase = 220,
            deputadosCentrao = 190,
            deputadosOposicao = 103,
            capitalPolitico = 85,
            indiceCorrupcaoPercebida = 36.0,
            confiancaInvestidorPct = 62.0,
            riscoImpeachmentPct = 8.0,
            emendasLiberadasMesBrl = 4_330_000_000.0,
            estabilidadeInstitucionalPct = 70.0
        )

        val ministries = listOf(
            Ministry(
                id = MinistryType.FAZENDA,
                nome = "Ministério da Fazenda e Planejamento",
                sigla = "MF",
                ministroNome = "Fernando Haddad",
                perfil = MinisterProfile.TECNICO,
                orcamentoCusteioAnualBrl = 12_000_000_000.0,
                orcamentoInvestimentoAnualBrl = 8_000_000_000.0,
                eficienciaSetorialPct = 74.0,
                satisfacaoCategoriaPct = 65.0,
                indicadorChaveNome = "Credibilidade do Arcabouço Fiscal",
                indicadorChaveValor = "71% Confiança de Mercado",
                descricaoPapel = "Regras fiscais, arrecadação da Receita Federal e metas de inflação."
            ),
            Ministry(
                id = MinistryType.SAUDE,
                nome = "Ministério da Saúde",
                sigla = "MS",
                ministroNome = "Nísia Trindade",
                perfil = MinisterProfile.TECNICO,
                orcamentoCusteioAnualBrl = 195_000_000_000.0,
                orcamentoInvestimentoAnualBrl = 37_000_000_000.0,
                eficienciaSetorialPct = 78.0,
                satisfacaoCategoriaPct = 70.0,
                indicadorChaveNome = "Leitos de UTI e Cobertura Vacinal",
                indicadorChaveValor = "83.5% Cobertura Nacional",
                descricaoPapel = "Gestão do SUS, aquisição de vacinas, medicamentos e expansão hospitalar."
            ),
            Ministry(
                id = MinistryType.EDUCACAO,
                nome = "Ministério da Educação",
                sigla = "MEC",
                ministroNome = "Camilo Santana",
                perfil = MinisterProfile.POLITICO,
                orcamentoCusteioAnualBrl = 152_000_000_000.0,
                orcamentoInvestimentoAnualBrl = 30_000_000_000.0,
                eficienciaSetorialPct = 72.0,
                satisfacaoCategoriaPct = 68.0,
                indicadorChaveNome = "Qualidade Básica (IDEB Médio)",
                indicadorChaveValor = "5.2 Pontos",
                descricaoPapel = "Pé-de-Meia, Fundeb, universidades federais e bolsas Capes/CNPq."
            ),
            Ministry(
                id = MinistryType.DEFESA,
                nome = "Ministério da Defesa / Forças Armadas",
                sigla = "MD",
                ministroNome = "José Múcio Monteiro",
                perfil = MinisterProfile.POLITICO,
                orcamentoCusteioAnualBrl = 108_000_000_000.0,
                orcamentoInvestimentoAnualBrl = 14_000_000_000.0,
                eficienciaSetorialPct = 79.0,
                satisfacaoCategoriaPct = 82.0,
                indicadorChaveNome = "Lealdade Institucional e Prontidão",
                indicadorChaveValor = "88% Estabilidade Militar",
                descricaoPapel = "Comando do Exército, Marinha, Aeronáutica e soberania territorial."
            ),
            Ministry(
                id = MinistryType.JUSTICA,
                nome = "Ministério da Justiça e Segurança Pública",
                sigla = "MJSP",
                ministroNome = "Ricardo Lewandowski",
                perfil = MinisterProfile.TECNICO,
                orcamentoCusteioAnualBrl = 24_000_000_000.0,
                orcamentoInvestimentoAnualBrl = 9_000_000_000.0,
                eficienciaSetorialPct = 75.0,
                satisfacaoCategoriaPct = 64.0,
                indicadorChaveNome = "Efetivo Integrado e Combate a Facções",
                indicadorChaveValor = "15.4k Agentes Federais",
                descricaoPapel = "Polícia Federal, PRF, penitenciárias federais e cooperação com os Estados."
            ),
            Ministry(
                id = MinistryType.INFRAESTRUTURA,
                nome = "Ministério dos Transportes e Infraestrutura",
                sigla = "MTI",
                ministroNome = "Renan Filho",
                perfil = MinisterProfile.POLITICO,
                orcamentoCusteioAnualBrl = 18_000_000_000.0,
                orcamentoInvestimentoAnualBrl = 60_000_000_000.0,
                eficienciaSetorialPct = 68.0,
                satisfacaoCategoriaPct = 60.0,
                indicadorChaveNome = "Custo Logístico do Frete Agro",
                indicadorChaveValor = "R$ 310 / tonelada",
                descricaoPapel = "Malha rodoviária, ferrovias, dragagem de portos e concessões do PAC."
            ),
            Ministry(
                id = MinistryType.MEIO_AMBIENTE,
                nome = "Ministério do Meio Ambiente e Clima",
                sigla = "MMA",
                ministroNome = "Marina Silva",
                perfil = MinisterProfile.TECNICO,
                orcamentoCusteioAnualBrl = 6_500_000_000.0,
                orcamentoInvestimentoAnualBrl = 4_000_000_000.0,
                eficienciaSetorialPct = 81.0,
                satisfacaoCategoriaPct = 73.0,
                indicadorChaveNome = "Taxa de Desmatamento na Amazônia",
                indicadorChaveValor = "-28% vs ano anterior",
                descricaoPapel = "Ibama, ICMBio, Fundo Amazônia e atração de créditos de carbono globais."
            ),
            Ministry(
                id = MinistryType.RELACOES_EXTERIORES,
                nome = "Ministério das Relações Exteriores",
                sigla = "MRE",
                ministroNome = "Mauro Vieira",
                perfil = MinisterProfile.TECNICO,
                orcamentoCusteioAnualBrl = 4_800_000_000.0,
                orcamentoInvestimentoAnualBrl = 1_200_000_000.0,
                eficienciaSetorialPct = 80.0,
                satisfacaoCategoriaPct = 76.0,
                indicadorChaveNome = "Acordos Comerciais e Atração de IDE",
                indicadorChaveValor = "USD 68 bi Investimento Externo",
                descricaoPapel = "Itamaraty, acordos bilaterais, negociações Mercosul-UE e cúpula dos BRICS."
            ),
            Ministry(
                id = MinistryType.DESENVOLVIMENTO_SOCIAL,
                nome = "Ministério do Desenvolvimento e Assistência Social",
                sigla = "MDS",
                ministroNome = "Wellington Dias",
                perfil = MinisterProfile.POLITICO,
                orcamentoCusteioAnualBrl = 240_000_000_000.0,
                orcamentoInvestimentoAnualBrl = 45_000_000_000.0,
                eficienciaSetorialPct = 76.0,
                satisfacaoCategoriaPct = 80.0,
                indicadorChaveNome = "Famílias no Bolsa Família / BPC",
                indicadorChaveValor = "20.8 Milhões de Lares",
                descricaoPapel = "Transferência de renda, erradicação da fome extrema e cadastro único."
            )
        )

        val enterprises = listOf(
            StateEnterprise(
                id = "petrobras",
                nome = "Petróleo Brasileiro S.A. (Petrobras)",
                setor = "Energia e Petróleo",
                regime = EnterpriseRegime.ECONOMIA_MISTA,
                participacaoUniaoVotoPct = 50.26,
                lucroMedioAnualBrl = 120_000_000_000.0,
                dividendosRepassadosTesouroBrl = 45_000_000_000.0,
                funcaoEstrategica = "Exploração de pré-sal, refino, abastecimento nacional e geração de dividendos e royalties para a União.",
                politicaPreco = PricePolicy.PPI_INTERNACIONAL
            ),
            StateEnterprise(
                id = "bb",
                nome = "Banco do Brasil S.A.",
                setor = "Financeiro / Bancário",
                regime = EnterpriseRegime.ECONOMIA_MISTA,
                participacaoUniaoVotoPct = 50.0,
                lucroMedioAnualBrl = 35_000_000_000.0,
                dividendosRepassadosTesouroBrl = 14_000_000_000.0,
                funcaoEstrategica = "Financiamento do Agronegócio (Plano Safra), crédito consignado e suporte bancário estatal."
            ),
            StateEnterprise(
                id = "caixa",
                nome = "Caixa Econômica Federal",
                setor = "Financeiro / Habitação",
                regime = EnterpriseRegime.TOTALMENTE_PUBLICA,
                participacaoUniaoVotoPct = 100.0,
                lucroMedioAnualBrl = 11_000_000_000.0,
                dividendosRepassadosTesouroBrl = 4_500_000_000.0,
                funcaoEstrategica = "Operacionalização do Bolsa Família, FGTS, Loterias Federais e crédito imobiliário de baixa renda (MCMV)."
            ),
            StateEnterprise(
                id = "bndes",
                nome = "BNDES",
                setor = "Desenvolvimento e Fomento",
                regime = EnterpriseRegime.TOTALMENTE_PUBLICA,
                participacaoUniaoVotoPct = 100.0,
                lucroMedioAnualBrl = 12_000_000_000.0,
                dividendosRepassadosTesouroBrl = 6_000_000_000.0,
                funcaoEstrategica = "Financiamento de longo prazo com juros competitivos para infraestrutura, indústria de transformação e exportações."
            ),
            StateEnterprise(
                id = "correios",
                nome = "Empresa Brasileira de Correios e Telégrafos (ECT)",
                setor = "Logística e Serviços Postais",
                regime = EnterpriseRegime.TOTALMENTE_PUBLICA,
                participacaoUniaoVotoPct = 100.0,
                lucroMedioAnualBrl = -1_500_000_000.0, // Déficit
                dividendosRepassadosTesouroBrl = 0.0,
                funcaoEstrategica = "Monopólio postal constitucional e logística em municípios remotos. Enfrenta déficit operacional."
            ),
            StateEnterprise(
                id = "embrapa",
                nome = "Embrapa (Pesquisa Agropecuária)",
                setor = "Ciência e Tecnologia Agrícola",
                regime = EnterpriseRegime.DEPENDENTE_TESOURO,
                participacaoUniaoVotoPct = 100.0,
                lucroMedioAnualBrl = 0.0,
                dividendosRepassadosTesouroBrl = 0.0,
                funcaoEstrategica = "Pesquisa e desenvolvimento para liderança mundial do agronegócio e sustentabilidade tropical."
            ),
            StateEnterprise(
                id = "enbpar",
                nome = "ENBPar (Energia Nuclear e Binacional)",
                setor = "Energia Nuclear e Hidroelétrica",
                regime = EnterpriseRegime.TOTALMENTE_PUBLICA,
                participacaoUniaoVotoPct = 100.0,
                lucroMedioAnualBrl = 2_500_000_000.0,
                dividendosRepassadosTesouroBrl = 1_200_000_000.0,
                funcaoEstrategica = "Gestão da Eletronuclear (Angra 1, 2 e 3) e da parte brasileira de Itaipu Binacional."
            ),
            StateEnterprise(
                id = "dataprev_serpro",
                nome = "Dataprev & SERPRO",
                setor = "Tecnologia da Informação e Segurança de Dados",
                regime = EnterpriseRegime.TOTALMENTE_PUBLICA,
                participacaoUniaoVotoPct = 100.0,
                lucroMedioAnualBrl = 800_000_000.0,
                dividendosRepassadosTesouroBrl = 350_000_000.0,
                funcaoEstrategica = "Bases soberanas de dados do INSS, Receita Federal, CPF, Identidade Digital e Gov.br."
            ),
            StateEnterprise(
                id = "dependentes_tesouro",
                nome = "Bloco de 18 Estatais Dependentes do Tesouro",
                setor = "Diversos (Ebserh, CPRM, Imbel, Ceitec, etc.)",
                regime = EnterpriseRegime.DEPENDENTE_TESOURO,
                participacaoUniaoVotoPct = 100.0,
                lucroMedioAnualBrl = -25_000_000_000.0, // Custo direto de ancoragem do Tesouro
                dividendosRepassadosTesouroBrl = 0.0,
                funcaoEstrategica = "Estruturas que não geram receita própria e demandam R$ 25 bilhões/ano do Tesouro para folha e custeio."
            )
        )

        val bills = listOf(
            CongressionalBill(
                id = "reforma_tributaria",
                titulo = "Regulamentação da Reforma Tributária (IBS/CBS)",
                tipo = BillType.LEI_COMPLEMENTAR,
                descricao = "Simplifica 5 tributos em IVA Dual com alíquota padrão, cashback para baixa renda e fim da guerra fiscal.",
                impactoFiscalAnualBrl = 18_000_000_000.0,
                impactoAprovacaoGeral = 3.5,
                impactoCapitalPoliticoNecessario = 20,
                votosBaseEsperados = 265,
                status = BillStatus.NA_GAVETA
            ),
            CongressionalBill(
                id = "reforma_administrativa",
                titulo = "Reforma Administrativa & Teto dos Super-Salários",
                tipo = BillType.PEC,
                descricao = "Corta penduricalhos do topo do funcionalismo e moderniza carreiras. Economiza R$ 42 bi/ano a médio prazo.",
                impactoFiscalAnualBrl = 42_000_000_000.0,
                impactoAprovacaoGeral = -2.0,
                impactoCapitalPoliticoNecessario = 40,
                votosBaseEsperados = 295,
                status = BillStatus.NA_GAVETA
            ),
            CongressionalBill(
                id = "isencao_ir_5k",
                titulo = "Isenção do Imposto de Renda até R$ 5.000",
                tipo = BillType.LEI_ORDINARIA,
                descricao = "Promessa de campanha que alivia a classe média trabalhadora. Custo fiscal de R$ 38 bi/ano se não houver compensação.",
                impactoFiscalAnualBrl = -38_000_000_000.0,
                impactoAprovacaoGeral = 6.0,
                impactoCapitalPoliticoNecessario = 15,
                votosBaseEsperados = 340,
                status = BillStatus.NA_GAVETA
            ),
            CongressionalBill(
                id = "arcabouco_gatilhos",
                titulo = "Gatilhos Automáticos de Controle da Dívida Pública",
                tipo = BillType.LEI_COMPLEMENTAR,
                descricao = "Trava reajustes e contratações se a Dívida passar de 85% do PIB. Acalma os mercados e reduz risco-país.",
                impactoFiscalAnualBrl = 30_000_000_000.0,
                impactoAprovacaoGeral = -1.5,
                impactoCapitalPoliticoNecessario = 25,
                votosBaseEsperados = 270,
                status = BillStatus.NA_GAVETA
            ),
            CongressionalBill(
                id = "combate_crime",
                titulo = "Marco Nacional de Combate às Facções & Fronteiras",
                tipo = BillType.LEI_ORDINARIA,
                descricao = "Endurece o isolamento prisional de lideranças e bloqueio de bens. Reduz taxa de homicídios e melhora aprovação da Classe C.",
                impactoFiscalAnualBrl = -4_000_000_000.0,
                impactoAprovacaoGeral = 4.5,
                impactoCapitalPoliticoNecessario = 15,
                votosBaseEsperados = 360,
                status = BillStatus.NA_GAVETA
            ),
            CongressionalBill(
                id = "privatizacao_dependentes",
                titulo = "Saneamento e Fusão de Estatais Dependentes",
                tipo = BillType.LEI_ORDINARIA,
                descricao = "Extingue cabides de emprego e funde empresas obsoletas, aliviando R$ 18 bi/ano de dreno no caixa federal.",
                impactoFiscalAnualBrl = 18_000_000_000.0,
                impactoAprovacaoGeral = 1.0,
                impactoCapitalPoliticoNecessario = 30,
                votosBaseEsperados = 250,
                status = BillStatus.NA_GAVETA
            )
        )

        val initialHistory = listOf(
            TurnHistoryItem(
                mes = 1,
                anoCalendario = 2026,
                nomeMes = "Janeiro de 2026",
                pibTrilhoes = 12.70,
                dividaPctPib = 82.9,
                inflacaoPct = 4.22,
                selicPct = 14.00,
                aprovacaoPct = 48.0,
                cambioUsd = 5.42,
                riscoPaisCds = 210.0,
                resultadoPrimarioBi = -65.0
            )
        )

        val initialNews = listOf(
            "Posse Presidencial: Novo mandato se inicia em Brasília sob expectativa do mercado e cobrança social.",
            "Relatório Focus do BC: Mercado projeta Selic em 14,00% e alerta para trajetória da Dívida Bruta (82,9% do PIB).",
            "Orçamento Engessado: Gastos Obrigatórios atingem 93,5%, deixando apenas R$ 188 bi para investimentos livres.",
            "Congresso Nacional: Presidentes da Câmara e Senado cobram liberação regular de emendas parlamentares."
        )

        return GameState(
            mesAtual = 1,
            anoAtual = 2026,
            economicMetrics = initialEcon,
            budgetMetrics = initialBudget,
            socialMetrics = initialSocial,
            politicalMetrics = initialPol,
            ministries = ministries,
            enterprises = enterprises,
            bills = bills,
            history = initialHistory,
            noticiasRecentes = initialNews
        )
    }
}
