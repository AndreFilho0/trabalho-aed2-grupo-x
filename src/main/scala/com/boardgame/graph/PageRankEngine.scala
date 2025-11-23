package com.boardgame.graph

import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import com.boardgame.graph.GraphBuilder.GameVertex
import com.boardgame.models.PageRankResult
import com.boardgame.utils.Logger

object PageRankEngine {
  
 
  def computePageRank(
  graph: Graph[GameVertex, Double],
  tolerance: Double,
  maxIterations: Int
): RDD[(VertexId, Double)] = {

  val pr = if (tolerance > 0)
    graph.pageRank(tolerance)
  else
    graph.staticPageRank(maxIterations)

  pr.vertices
}

    
 
  private def repartitionGraphForDistribution(
    graph: Graph[GameVertex, Double],
    numPartitions: Int
  ): Graph[GameVertex, Double] = {
    
    Logger.info(s"Reparticionando grafo em $numPartitions partições...")
    
    val repartitionedVertices = graph.vertices.repartition(numPartitions)
    val repartitionedEdges = graph.edges.repartition(numPartitions)
    
    val defaultVertex = GraphBuilder.GameVertex(0L, "Unknown", 0, 0.0, 0, 0)
    Graph(repartitionedVertices, repartitionedEdges, defaultVertex)
  }
  
 
  def combinePageRankWithMetadata(
    pageRankResults: RDD[(VertexId, Double)],
    graph: Graph[GameVertex, Double]
  ): RDD[PageRankResult] = {
    
    Logger.info("Combinando PageRank com metadados dos jogos (join distribuído)...")
    
    val vertexData = graph.vertices
    
  
    val combined = pageRankResults
      .join(vertexData) // MapReduce shuffle
      .map { case (gameId, (prScore, vertex)) =>
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
    
    combined.cache()
    Logger.success(s"✓ ${combined.count()} resultados combinados")
    combined
  }
  
 
  def getTopGames(
    results: RDD[PageRankResult],
    topK: Int = 20
  ): Array[PageRankResult] = {
    
    Logger.info(s"Selecionando top $topK jogos (sort distribuído)...")
    
    val sorted = results
      .sortBy(_.pageRankScore, ascending = false)
      .take(topK)
    
    Logger.success(s"✓ Top $topK selecionados")
    sorted
  }
  
 
  def getInfluentialGames(
    results: RDD[PageRankResult],
    minScore: Double = 0.01
  ): Array[PageRankResult] = {
    
    Logger.info(s"Filtrando jogos com score > $minScore...")
    
    val filtered = results
      .filter(_.pageRankScore > minScore) // Operação distribuída
      .sortBy(_.pageRankScore, ascending = false)
      .collect()
    
    Logger.success(s"✓ ${filtered.length} jogos influentes encontrados")
    filtered
  }
  
  
  def computePageRankStatistics(
    results: RDD[PageRankResult]
  ): Map[String, Double] = {
    
    Logger.info("Computando estatísticas de PageRank (agregação distribuída)...")
    
    val stats = results
      .map(r => (r.pageRankScore, 1L))
      .aggregate((0.0, 0L, Double.MaxValue, Double.MinValue))(
        // Função de agregação local (map-side)
        { case ((sum, count, min, max), (score, _)) =>
          (sum + score, count + 1, math.min(min, score), math.max(max, score))
        },
        // Função de agregação global (reduce-side)
        { case ((sum1, count1, min1, max1), (sum2, count2, min2, max2)) =>
          (sum1 + sum2, count1 + count2, math.min(min1, min2), math.max(max1, max2))
        }
      )
    
    val (totalSum, count, minScore, maxScore) = stats
    val avgScore = if (count > 0) totalSum / count else 0.0
    
    Map(
      "min" -> minScore,
      "max" -> maxScore,
      "avg" -> avgScore,
      "count" -> count.toDouble
    )
  }
  
  
  def analyzeInfluence(
    results: RDD[PageRankResult],
    gameGraph: Graph[GameVertex, Double]
  ): Unit = {
    
    Logger.info("Analisando influência dos jogos...")
    
   
    val inDegrees = gameGraph.inDegrees
    
  
    val outDegrees = gameGraph.outDegrees
    
    val influenceMetrics = results
      .map(r => r.gameId -> r.pageRankScore)
      .join(inDegrees)
      .map { case (gameId, (score, inDeg)) =>
        (gameId, ("score" -> score, "inDegree" -> inDeg.toDouble))
      }
    
    val avgInDegree = inDegrees
      .map(_._2.toDouble)
      .mean()
    
    Logger.success(s"✓ Grau médio de entrada: $avgInDegree")
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

