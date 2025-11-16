// ===============================================================
// 03 — ANALYTICS COM GRAPHX (PageRank, Communities, LPA, Degree)
// ===============================================================

import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import spark.implicits._
import org.apache.spark.graphx.lib.LabelPropagation

// ---------------------------------------------------------------
// 1. CARREGAR DATASETS
// ---------------------------------------------------------------

val nodesDF = spark.read.option("header", "true").csv("/app/dados/nodes.csv")
val edgesDF = spark.read.option("header", "true").csv("/app/dados/edges.csv")

val vertices: RDD[(VertexId, String)] =
  nodesDF.select("nodeId", "title").rdd.map { row =>
    (row.getString(0).toLong, row.getString(1))
  }

val edges: RDD[Edge[Double]] =
  edgesDF.select("src", "dst").rdd.map { row =>
    Edge(row.getString(0).toLong, row.getString(1).toLong, 1.0)
  }

val graph = Graph(vertices, edges)

println("Grafo carregado!")

// ---------------------------------------------------------------
// 2. PAGE RANK
// ---------------------------------------------------------------

val pagerank = graph.pageRank(0.001).vertices

val pagerankJoined = pagerank.join(vertices).map {
  case (id, (rank, title)) => (id, title, rank)
}

// salvar PageRank com colunas autoexplicativas
pagerankJoined
  .toDF("boardgame_id", "boardgame_title", "pagerank_score")
  .orderBy($"pagerank_score".desc)
  .write.mode("overwrite").option("header", "true").csv("/app/dados/output_pagerank")

println("PageRank salvo em /app/dados/output_pagerank")



// ---------------------------------------------------------------
// 4. LABEL PROPAGATION
// ---------------------------------------------------------------

val lpaGraph = LabelPropagation.run(graph, 10)

val lpaJoined = lpaGraph.vertices.join(vertices).map {
  case (id, (label, title)) => (id, title, label)
}

lpaJoined
  .toDF("boardgame_id", "boardgame_title", "community_label")
  .write.mode("overwrite").option("header", "true").csv("/app/dados/output_lpa")

println("Label Propagation salvo em /app/dados/output_lpa")


println("=====================================================")
println("   ✔ PROCESSO FINALIZADO — ARQUIVOS SALVOS!         ")
println("=====================================================")


