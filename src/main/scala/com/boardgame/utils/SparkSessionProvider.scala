package com.boardgame.utils

import org.apache.spark.sql.SparkSession

object SparkSessionProvider {
  
  private var session: Option[SparkSession] = None
  
  def getOrCreateSession(): SparkSession = {
    session match {
      case Some(s) => s
      case None =>
        val spark = SparkSession.builder()
          .appName("BoardGame-PageRank-Analysis")
          .master("local[*]")
          .config("spark.sql.shuffle.partitions", "8")
          .config("spark.graphx.numPartitions", "8")
          .config("spark.driver.memory", "2g")
          .config("spark.executor.memory", "2g")
          .getOrCreate()
        
        spark.sparkContext.setLogLevel("WARN")
        session = Some(spark)
        spark
    }
  }
  
  def stopSession(): Unit = {
    session.foreach(_.stop())
    session = None
  }
}
