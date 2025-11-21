package com.boardgame.utils

object ConfigLoader {
  
  case class AppConfig(
    inputJsonPath: String,
    outputCsvPath: String,
    outputJsonPath: String,
    prTolerance: Double,
    prMaxIterations: Int,
    topK: Int
  )
  
  def loadConfig(): AppConfig = {
    AppConfig(
      inputJsonPath = sys.env.getOrElse("INPUT_PATH", "/app/data/raw/bgg-games.json"),
      outputCsvPath = sys.env.getOrElse("OUTPUT_CSV", "/app/data/output/pagerank-results.csv"),
      outputJsonPath = sys.env.getOrElse("OUTPUT_JSON", "/app/data/output/pagerank-results.json"),
      prTolerance = sys.env.getOrElse("PR_TOLERANCE", "0.001").toDouble,
      prMaxIterations = sys.env.getOrElse("PR_ITERATIONS", "30").toInt,
      topK = sys.env.getOrElse("TOP_K", "20").toInt
    )
  }
}
