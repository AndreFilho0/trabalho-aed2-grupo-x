// Script Scala para rodar GraphX com PageRank e Label Propagation
// Execute com: spark-shell -i este_arquivo.scala

import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

println("Carregando Grafo de Jogos de Tabuleiro...")

// 1. CARREGAR VÉRTICES (nodes.csv)
val verticesRaw = spark.read
  .option("header", "true")
  .csv("/app/dados/nodes.csv")

val vertices: RDD[(VertexId, String)] = verticesRaw
  .rdd
  .map(row => (row.getString(0).toLong, row.getString(1)))

println(s"Vértices carregados: ${vertices.count()}")

// 2. CARREGAR ARESTAS (edges.csv)
val edgesRaw = spark.read
  .option("header", "true")
  .csv("/app/dados/edges.csv")

val edges: RDD[Edge[String]] = edgesRaw
  .rdd
  .map(row => Edge(
    row.getString(0).toLong,
    row.getString(1).toLong,
    row.getString(2)
  ))

println(s"Arestas carregadas: ${edges.count()}")

// 3. CRIAR GRAFO
val graph = Graph(vertices, edges)
println(s"Grafo criado com ${graph.vertices.count()} vértices e ${graph.edges.count()} arestas")

// 4. EXECUTAR PAGERANK
println("\nExecutando PageRank...")
val ranks = graph.pageRank(0.001).vertices

// Juntar com os nomes dos jogos
val ranksByGame = vertices.join(ranks).map {
  case (id, (name, rank)) => (name, rank)
}

println("\nTop 10 Jogos Mais Influentes (PageRank):")
ranksByGame
  .sortBy(_._2, ascending = false)
  .take(10)
  .foreach { case (name, rank) => 
    println(f"  $name%s -> ${rank}%.4f")
  }

// 5. EXECUTAR LABEL PROPAGATION (Detecção de Comunidades)
println("\nExecutando Label Propagation Algorithm...")
val communities = graph.labelPropagation(5).vertices

// Ver distribuição de comunidades
val communityCounts = communities
  .map { case (id, community) => (community, 1) }
  .reduceByKey(_ + _)
  .sortBy(_._2, ascending = false)

println("\nDistribuição de Comunidades:")
communityCounts.take(10).foreach { case (communityId, count) =>
  println(s"  Comunidade $communityId: $count jogos")
}

// 6. JOGOS DA MESMA COMUNIDADE (exemplo)
val gamesWithCommunities = vertices.join(communities).map {
  case (id, (name, community)) => (community, name)
}

println("\nExemplo: Jogos da Comunidade 0:")
gamesWithCommunities
  .filter(_._1 == 0)
  .take(10)
  .foreach { case (_, name) => println(s"  - $name") }

// 7. SALVAR RESULTADOS
println("\nSalvando resultados...")

// Salvar PageRank
ranksByGame
  .toDF("game", "pagerank")
  .write
  .mode("overwrite")
  .csv("/app/dados/results_pagerank.csv")

// Salvar Comunidades
gamesWithCommunities
  .toDF("community", "game")
  .write
  .mode("overwrite")
  .csv("/app/dados/results_communities.csv")

println("Resultados salvos em /app/dados/")
println("\nAnálise completa!")
