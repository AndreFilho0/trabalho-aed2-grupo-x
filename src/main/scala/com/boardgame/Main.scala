package com.boardgame

import com.boardgame.data.{DataLoader, DataWriter}
import com.boardgame.graph.{GraphBuilder, PageRankEngine}
import com.boardgame.parsers.JsonParser
import com.boardgame.utils.{ConfigLoader, Logger, SparkSessionProvider}

object Main {

  def main(args: Array[String]): Unit = {

    val config = ConfigLoader.loadConfig()
    val spark = SparkSessionProvider.getOrCreateSession()

    try {
   
      Logger.info(" INICIANDO CÁLCULO DISTRIBUÍDO DE PAGERANK")
   

      Logger.info("[1] Carregando dataset JSON...")
      val rawGames = DataLoader.loadBoardGamesFromJson(spark, config.inputJsonPath)

      val (isValid, message) = JsonParser.validateGameData(rawGames)
      if (!isValid) {
        Logger.error(message)
        return
      }
      Logger.success(message)

     
      Logger.info("[2] Normalizando e enriquecendo os dados...")
      val enrichedGames = DataLoader.enrichGamesData(rawGames)

      
      Logger.info("[3] Construindo grafo distribuído de recomendações...")
      val graph = GraphBuilder.buildGameNetwork(enrichedGames)

     
      Logger.info("[4] Calculando PageRank distribuído via GraphX...")
      val pageRank = PageRankEngine.computePageRank(
        graph,
        tolerance = config.prTolerance,
        maxIterations = config.prMaxIterations
      )

      
      Logger.info("[5] Combinando PageRank com metadados dos jogos...")
      val combinedResultsRDD = PageRankEngine.combinePageRankWithMetadata(pageRank, graph)

      
      Logger.info("[6] Filtrando resultados inválidos e ordenando por PageRank Score...")
      
      val cleanAndSortedResults = combinedResultsRDD
        .filter(_.gameTitle != "Unknown") 
        .sortBy(_.pageRankScore, ascending = false)
        
      Logger.success(s"✓ ${cleanAndSortedResults.count()} resultados válidos encontrados após a filtragem.")


      DataWriter.savePageRankResults(cleanAndSortedResults.collect(), config.outputCsvPath)

           Logger.success("      PROCESSAMENTO DE PAGERANK CONCLUÍDO!")


    } catch {
      case e: Exception =>
        Logger.error(s"Erro: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      SparkSessionProvider.stopSession()
    }
  }
}


