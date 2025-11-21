package com.boardgame.data

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import com.boardgame.parsers.JsonParser

object DataLoader {
  
  def loadBoardGamesFromJson(
    sparkSession: SparkSession,
    jsonPath: String
  ): DataFrame = {
    
    val rawDf = JsonParser.parseGamesFromJson(sparkSession, jsonPath)
    val processedDf = JsonParser.extractBoardGames(rawDf)
    
    processedDf
  }
  
  def enrichGamesData(gamesDF: DataFrame): DataFrame = {
    import gamesDF.sparkSession.implicits._
    
    gamesDF
      .withColumn("avgPlayTime", (col("minplaytime") + col("maxplaytime")) / 2)
      .withColumn("playerRange", concat_ws("-", col("minplayers"), col("maxplayers")))
      .withColumn("recommendationCount", size(col("fans_liked")))
      .withColumn("ratingCategory", 
        when(col("rating_value") >= 8.0, "Excelente")
          .when(col("rating_value") >= 7.0, "Muito Bom")
          .when(col("rating_value") >= 6.0, "Bom")
          .otherwise("Aceitável")
      )
  }
  def getGameStatistics(gamesDF: DataFrame): Map[String, Any] = {
  val stats = gamesDF.agg(
    count("id").alias("total_games"),
    avg("rating_value").alias("avg_rating"),
    avg("recommendationCount").alias("avg_recommendations"),
    min("year").alias("earliest_year"),
    max("year").alias("latest_year")
  ).first()

  Map(
    "Total de Jogos"        -> stats.getAs[Long]("total_games"),
    "Rating Médio"          -> f"${stats.getAs[Double]("avg_rating")}%.2f",
    "Recomendações Médias"  -> f"${stats.getAs[Double]("avg_recommendations")}%.1f",
    "Ano Mais Antigo"       -> stats.getAs[Int]("earliest_year"),
    "Ano Mais Recente"      -> stats.getAs[Int]("latest_year")
  )
}
    
  
  def printStatistics(stats: Map[String, Any]): Unit = {
    println("\n" + "="*60)
    println("ESTATÍSTICAS DO DATASET")
    println("="*60)
    stats.foreach { case (key, value) =>
      println(f"$key%30s: $value")
    }
    println("="*60 + "\n")
  }
}
