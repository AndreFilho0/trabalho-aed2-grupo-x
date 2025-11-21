package com.boardgame.graph

import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.DataFrame
import com.boardgame.models.BoardGame

object GraphBuilder {
  
  case class GameVertex(
    id: Long,
    title: String,
    year: Int,
    rating: Double,
    rank: Int,
    recommendationCount: Int
  )
  
  def buildGameNetwork(gamesDF: DataFrame): Graph[GameVertex, Double] = {
    
    import gamesDF.sparkSession.implicits._
    
    val sparkContext = gamesDF.sparkSession.sparkContext
    
    val vertices: RDD[(VertexId, GameVertex)] = gamesDF.rdd.map { row =>
      val id = row.getAs[Long]("id")
      val title = row.getAs[String]("title")
      val year = row.getAs[Int]("year")
      val rating = row.getAs[Double]("rating_value")
      val rank = row.getAs[Int]("rank")
      val recCount = row.getAs[Int]("recommendationCount")
      
      (id, GameVertex(id, title, year, rating, rank, recCount))
    }
    
    val edges: RDD[Edge[Double]] = gamesDF.rdd.flatMap { row =>
      val sourceId = row.getAs[Long]("id")
      val recommendedIds = row.getAs[scala.collection.Seq[Long]]("fans_liked")
      
      if (recommendedIds != null) {
        recommendedIds.map { targetId =>
          Edge(sourceId, targetId, 1.0)
        }
      } else {
        Seq()
      }
    }
    
    val defaultVertex = GameVertex(0L, "Unknown", 0, 0.0, 0, 0)
    Graph(vertices, edges, defaultVertex)
  }
  
  def getGraphStatistics(graph: Graph[GameVertex, Double]): Map[String, Any] = {
    Map(
      "Total de Vértices (Jogos)" -> graph.numVertices,
      "Total de Arestas (Recomendações)" -> graph.numEdges,
      "Grau Médio de Entrada" -> (graph.numEdges.toDouble / graph.numVertices),
      "Densidade do Grafo" -> (graph.numEdges.toDouble / (graph.numVertices * (graph.numVertices - 1)))
    )
  }
  
  def printGraphStatistics(stats: Map[String, Any]): Unit = {
    println("\n" + "="*60)
    println("ESTATÍSTICAS DO GRAFO")
    println("="*60)
    stats.foreach { case (key, value) =>
      val formatted = value match {
        case d: Double => f"$d%.6f"
        case _ => value.toString
      }
      println(f"$key%40s: $formatted")
    }
    println("="*60 + "\n")
  }
}
