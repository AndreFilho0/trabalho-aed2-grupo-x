package com.boardgame

import com.boardgame.data.{DataLoader, DataWriter}
import com.boardgame.graph.{GraphBuilder, GraphAnalyzer, PageRankEngine}
import com.boardgame.parsers.JsonParser
import com.boardgame.utils.{ConfigLoader, Logger, SparkSessionProvider}

object Main {
  
  def main(args: Array[String]): Unit = {
    val config = ConfigLoader.loadConfig()
    val spark = SparkSessionProvider.getOrCreateSession()
    
    try {
      Logger.info("="*80)
      Logger.info("INICIANDO ANÁLISE DE BOARD GAMES COM PAGERANK")
      Logger.info("="*80)
      
      Logger.info("[FASE 1] Carregando dados JSON...")
      val rawGames = DataLoader.loadBoardGamesFromJson(spark, config.inputJsonPath)
      
      val (isValid, message) = JsonParser.validateGameData(rawGames)
      if (!isValid) {
        Logger.error(message)
        return
      }
      Logger.success(message)
      
      Logger.info("[FASE 2] Enriquecendo dados dos jogos...")
      val enrichedGames = DataLoader.enrichGamesData(rawGames)
      
      val stats = DataLoader.getGameStatistics(enrichedGames)
      DataLoader.printStatistics(stats)
      
      Logger.info("[FASE 3] Construindo grafo de recomendações...")
      val gameGraph = GraphBuilder.buildGameNetwork(enrichedGames)
      
      val graphStats = GraphBuilder.getGraphStatistics(gameGraph)
      GraphBuilder.printGraphStatistics(graphStats)
      
      Logger.info("[FASE 4] Computando PageRank...")
      val pageRankRaw = PageRankEngine.computePageRank(
        gameGraph,
        tolerance = config.prTolerance,
        maxIterations = config.prMaxIterations
      )
      
      Logger.info("[FASE 5] Combinando PageRank com metadados dos jogos...")
      val pageRankResults = PageRankEngine.combinePageRankWithMetadata(pageRankRaw, gameGraph)
      val topGames = PageRankEngine.getTopGames(pageRankResults, config.topK)
      
      Logger.success(s"✓ PageRank calculado com sucesso!")
      
      Logger.info("[FASE 6] Analisando estrutura do grafo...")
      GraphAnalyzer.analyzeInOutDegree(gameGraph)
      GraphAnalyzer.identifyHubs(gameGraph)
      GraphAnalyzer.findStronglyConnectedComponents(gameGraph)
      
      Logger.info("[FASE 7] Comparando PageRank com Ranking Oficial...")
      PageRankEngine.compareWithRanking(topGames)
      
      Logger.info("[FASE 8] Exibindo resultados...")
      DataWriter.printTopResults(topGames, config.topK)
      
      Logger.info("[FASE 9] Salvando resultados...")
      DataWriter.savePageRankResults(topGames, config.outputCsvPath)
      
      Logger.success("="*80)
      Logger.success("ANÁLISE CONCLUÍDA COM SUCESSO!")
      Logger.success("="*80)
      
    } catch {
      case e: Exception =>
        Logger.error(s"Erro durante execução: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      SparkSessionProvider.stopSession()
    }
  }
}
