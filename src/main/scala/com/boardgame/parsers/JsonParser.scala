package com.boardgame.parsers

import scala.util.{Try, Failure, Success}
import org.apache.spark.sql.{DataFrame, SparkSession}
import com.boardgame.models._

object JsonParser {
  
  def parseGamesFromJson(
    sparkSession: SparkSession,
    jsonPath: String
  ): DataFrame = {
    sparkSession.read
      .option("multiline", "true")
      .option("mode", "PERMISSIVE")
      .json(jsonPath)
  }
  
  def extractBoardGames(df: DataFrame): DataFrame = {
    import df.sparkSession.implicits._
    import org.apache.spark.sql.functions._
    
    df.select(
      col("id").cast("long"),
      col("title").cast("string"),
      col("year").cast("int"),
      col("rank").cast("int"),
      col("minplayers").cast("int"),
      col("maxplayers").cast("int"),
      col("minplaytime").cast("int"),
      col("maxplaytime").cast("int"),
      col("minage").cast("int"),
      col("rating.rating").cast("double").alias("rating_value"),
      col("rating.num_of_reviews").cast("long").alias("num_reviews"),
      col("recommendations.fans_liked").alias("fans_liked"),
      col("types.categories").alias("categories"),
      col("types.mechanics").alias("mechanics")
    )
  }
  
  def validateGameData(df: DataFrame): (Boolean, String) = {
    try {
      val requiredColumns = Seq(
        "id", "title", "year", "rank", "rating_value", "fans_liked"
      )
      
      val missingColumns = requiredColumns.diff(df.columns.toSeq)
      
      if (missingColumns.nonEmpty) {
        (false, s"Colunas faltando: ${missingColumns.mkString(", ")}")
      } else {
        val count = df.count()
        if (count == 0) {
          (false, "Dataset está vazio")
        } else {
          (true, s"Dataset validado: $count jogos carregados")
        }
      }
    } catch {
      case e: Exception => (false, s"Erro na validação: ${e.getMessage}")
    }
  }
}
