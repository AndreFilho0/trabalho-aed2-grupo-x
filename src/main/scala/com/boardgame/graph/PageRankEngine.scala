package com.boardgame.graph

import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import com.boardgame.graph.GraphBuilder.GameVertex
import com.boardgame.models.PageRankResult

object PageRankEngine {
  
  def computePageRank(
  graph: Graph[GameVertex, Double],
  tolerance: Double = 0.001,
  maxIterations: Int = 30
): RDD[(VertexId, Double)] = {
  
  println("\n[*] Calculando PageRank...")
  println(s"    Tolerância: $tolerance")
  println(s"    Máximo de Iterações: $maxIterations")

  val pageRankGraph =
    if (tolerance > 0) {
      // PageRank até convergência (modo recomendado)
      println("    ➜ Modo: Convergência (pageRank)")
      graph.pageRank(tolerance)
    } else {
      // PageRank com N iterações
      println("    ➜ Modo: Iterativo (staticPageRank)")
      graph.staticPageRank(maxIterations)
    }
  
  pageRankGraph.vertices
}

  
  def combinePageRankWithMetadata(
    pageRankResults: RDD[(VertexId, Double)],
    graph: Graph[GameVertex, Double]
  ): RDD[PageRankResult] = {
    
    val vertexData = graph.vertices
    
    pageRankResults.join(vertexData).map { case (gameId, (prScore, vertex)) =>
      PageRankResult(
        gameId = gameId,
        gameTitle = vertex.title,
        rank = vertex.rank,
        pageRankScore = prScore,
        rating = vertex.rating,
        year = vertex.year,
        recommendationCount = vertex.recommendationCount
      )
    }
  }
  
  def getTopGames(
    results: RDD[PageRankResult],
    topK: Int = 20
  ): Array[PageRankResult] = {
    
    results
      .sortBy(_.pageRankScore, ascending = false)
      .take(topK)
  }
  
  def getInfluentialGames(
    results: RDD[PageRankResult],
    minScore: Double = 0.01
  ): Array[PageRankResult] = {
    
    results
      .filter(_.pageRankScore > minScore)
      .sortBy(_.pageRankScore, ascending = false)
      .collect()
  }
  
  def compareWithRanking(
    pageRankResults: Array[PageRankResult]
  ): Unit = {
    
    println("\n" + "="*80)
    println("ANÁLISE COMPARATIVA: PageRank vs Ranking Oficial (BGG)")
    println("="*80)
    
    val topByPageRank = pageRankResults.take(10)
    val topByRanking = pageRankResults.sortBy(_.rank).take(10)
    
    println("\nTop 10 por PageRank (Influência na Rede):")
    topByPageRank.zipWithIndex.foreach { case (game, idx) =>
      println(f"${idx + 1}%2d. ${game.gameTitle}%-50s PR: ${game.pageRankScore}%.8f")
    }
    
    println("\nTop 10 por Ranking BGG (Qualidade):")
    topByRanking.zipWithIndex.foreach { case (game, idx) =>
      println(f"${idx + 1}%2d. ${game.gameTitle}%-50s Rank: ${game.rank}%5d")
    }
    
    println("="*80 + "\n")
  }
}
