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

  val vertices = gamesDF.rdd.map { row =>
    val id = row.getAs[Long]("id")
    val title = row.getAs[String]("title")
    val rating = row.getAs[Double]("rating_value")
    val year = row.getAs[Int]("year")
    val rank = row.getAs[Int]("rank")
    val recCount = row.getAs[Int]("recommendationCount")

    (id, GameVertex(id, title, year, rating, rank, recCount))
  }

  val edges = gamesDF.rdd.flatMap { row =>
    val source = row.getAs[Long]("id")
    val recs = row.getAs[Seq[Long]]("fans_liked")
    if (recs != null) recs.map(target => Edge(source, target, 1.0)) else Seq()
  }

  val defaultVertex = GameVertex(0L, "Unknown", 0, 0.0, 0, 0)

  Graph(vertices, edges, defaultVertex)
    .partitionBy(PartitionStrategy.EdgePartition2D)
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
