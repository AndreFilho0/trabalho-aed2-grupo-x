package com.boardgame.graph

import org.apache.spark.graphx._
import com.boardgame.graph.GraphBuilder.GameVertex

object GraphAnalyzer {

  case class GameInfluence(
    gameId: Long,
    title: String,
    inDegree: Long,
    outDegree: Long,
    reciprocalLinks: Long
  )

  def analyzeInOutDegree(graph: Graph[GameVertex, Double]): Unit = {

    println("\n" + "=" * 60)
    println("ANÁLISE DE GRAU DE ENTRADA/SAÍDA")
    println("=" * 60)

    val inDegrees = graph.inDegrees
    val outDegrees = graph.outDegrees

    val avgIn = inDegrees.map(_._2).mean()
    val avgOut = outDegrees.map(_._2).mean()
    val maxIn = inDegrees.map(_._2).max()
    val maxOut = outDegrees.map(_._2).max()

    println(f"Grau de Entrada Médio    : $avgIn%.2f")
    println(f"Grau de Saída Médio      : $avgOut%.2f")
    println(f"Grau de Entrada Máximo   : $maxIn")
    println(f"Grau de Saída Máximo     : $maxOut")

    println("\nJogos Mais Recomendados (Alto Grau de Entrada):")
    inDegrees
      .sortBy(_._2, ascending = false)
      .take(10)
      .foreach { case (gameId, degree) =>
        val gameData = graph.vertices.filter(_._1 == gameId).collect()
        if (gameData.nonEmpty) {
          println(f"  - ${gameData(0)._2.title}%-50s: $degree%5d recomendações recebidas")
        }
      }

    println("\nJogos com Mais Recomendações (Alto Grau de Saída):")
    outDegrees
      .sortBy(_._2, ascending = false)
      .take(10)
      .foreach { case (gameId, degree) =>
        val gameData = graph.vertices.filter(_._1 == gameId).collect()
        if (gameData.nonEmpty) {
          println(f"  - ${gameData(0)._2.title}%-50s: $degree%5d recomendações feitas")
        }
      }

    println("=" * 60 + "\n")
  }

  def findStronglyConnectedComponents(graph: Graph[GameVertex, Double]): Unit = {

    println("\n[*] Analisando componentes fortemente conectadas...")

    val sccGraph = graph.stronglyConnectedComponents(20) // Corrigido
    val numComponents = sccGraph.vertices.map(_._2).distinct().count()

    println(s"✓ Encontrados $numComponents componentes fortemente conectados")

    val componentSizes = sccGraph.vertices
      .map { case (_, componentId) => (componentId, 1L) }
      .reduceByKey(_ + _)
      .sortBy(_._2, ascending = false)
      .collect()

    println("\nTop 5 Maiores Componentes:")
    componentSizes.take(5).zipWithIndex.foreach { case ((compId, size), idx) =>
      println(f"  ${idx + 1}. Componente $compId: $size jogos")
    }
  }

  def identifyHubs(graph: Graph[GameVertex, Double]): Unit = {

    println("\n" + "=" * 60)
    println("IDENTIFICAÇÃO DE HUBS (Nós Centrais)")
    println("=" * 60)

    val degrees = graph.degrees
    val avgDegree = degrees.map(_._2).mean()
    val stdDev = Math.sqrt(
      degrees.map(x => Math.pow(x._2 - avgDegree, 2)).mean()
    )

    val hubThreshold = avgDegree + stdDev

    println(f"Grau Médio: $avgDegree%.2f")
    println(f"Desvio Padrão: $stdDev%.2f")
    println(f"Limiar de Hub: $hubThreshold%.2f\n")

    val hubs = degrees
      .filter(_._2 > hubThreshold)
      .collect()

    println(s"Encontrados ${hubs.length} hubs:")
    hubs.sortBy(_._2)(Ordering[Int].reverse).take(15).foreach { case (gameId, degree) =>
      val gameData = graph.vertices.filter(_._1 == gameId).collect()
      if (gameData.nonEmpty) {
        val vertex = gameData(0)._2
        println(f"  - ${vertex.title}%-50s (Grau: $degree, Rating: ${vertex.rating}%.2f)")
      }
    }

    println("=" * 60 + "\n")
  }
}


