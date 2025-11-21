package com.boardgame.data

import org.apache.spark.sql.DataFrame
import com.boardgame.models.PageRankResult

object DataWriter {

  def savePageRankResults(
    results: Array[PageRankResult],
    outputPath: String
  ): Unit = {

    val csvContent = new StringBuilder()
    csvContent.append("GameID,Title,Rank,PageRankScore,Influence,Rating,Year,Recommendations\n")

    results.foreach { result =>
      val line =
        f"${result.gameId},${result.gameTitle},${result.rank}," +
        f"${result.pageRankScore}%.8f,${result.influence},${result.rating}," +
        f"${result.year},${result.recommendationCount}\n"
      csvContent.append(line)
    }

    val pw = new java.io.PrintWriter(outputPath)
    try pw.write(csvContent.toString) finally pw.close()

    println(s"\n✓ Resultados salvos em: $outputPath")
  }

  def saveJsonResults(
    results: Array[PageRankResult],
    outputPath: String
  ): Unit = {

    import org.json4s._
    import org.json4s.native.Serialization
    import org.json4s.native.Serialization.write
    import org.json4s.native.JsonMethods.{parse, pretty, render}

    implicit val formats = Serialization.formats(NoTypeHints)

    val json = write(results)

    val pw = new java.io.PrintWriter(outputPath)
    try pw.write(pretty(render(parse(json)))) finally pw.close()

    println(s"\n✓ Resultados JSON salvos em: $outputPath")
  }

  def printTopResults(results: Array[PageRankResult], topK: Int = 20): Unit = {
    println("\n" + "=" * 120)
    println("TOP 20 JOGOS MAIS INFLUENTES (Por PageRank)")
    println("=" * 120)

    printf("%4s %8s %12s %12s %8s %6s %-60s\n",
      "Pos", "Game ID", "PageRank", "Influence", "Rating", "Recs", "Title"
    )

    println("-" * 120)

    results.take(topK).zipWithIndex.foreach { case (result, idx) =>
      printf("%4d %8d %12.8f %12s %8.2f %6d %-60s\n",
        idx + 1,
        result.gameId,
        result.pageRankScore,
        result.influence,
        result.rating,
        result.recommendationCount,
        result.gameTitle.take(55)
      )
    }

    println("=" * 120 + "\n")
  }
}


