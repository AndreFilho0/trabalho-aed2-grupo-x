package com.boardgame.utils

import org.apache.spark.sql.SparkSession

object SparkSessionProvider {
  
  private var sparkSession: Option[SparkSession] = None
  
  def getOrCreateSession(): SparkSession = {
    sparkSession match {
      case Some(session) => session
      case None =>
        val session = createOptimizedSession()
        sparkSession = Some(session)
        session
    }
  }
  
  /**
    * Cria SparkSession com otimizações de performance, mas sem forçar
    * configurações de recursos e paralelismo, que virão do spark-submit.
    */
  private def createOptimizedSession(): SparkSession = {
    
    SparkSession
      .builder()
      .appName("BoardGame-PageRank-Distributed")

      .config("spark.sql.adaptive.enabled", "true") 
      .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .config("spark.kryoserializer.buffer.max", "512m")
      .config("spark.memory.fraction", "0.8") // 80% para RDD/cache
      .config("spark.memory.storageFraction", "0.5")
      .config("spark.rdd.compress", "true")
      .config("spark.shuffle.compress", "true")
      .config("spark.network.timeout", "300s")
      .config("spark.executor.heartbeatInterval", "10s")
      .config("spark.dynamicAllocation.enabled", "false") // Mantido para controle manual
      
            
      .getOrCreate()
  }
  
  def stopSession(): Unit = {
    sparkSession.foreach(_.stop())
    sparkSession = None
  }
  
 
  def getClusterInfo(): String = {
    val spark = getOrCreateSession()
    val sc = spark.sparkContext
    

    val numExecutors = sc.statusTracker.getExecutorInfos.length - 1
    
    s"""
      |╔════════════════════════════════════════╗
      |║           INFORMAÇÕES DO CLUSTER       ║
      |╠════════════════════════════════════════╣
      |║ Executores (Workers):    $numExecutors
      |║ Master URL:             ${sc.master}
      |║ Default Parallelism:     ${sc.defaultParallelism}
      |║ Shuffle Partitions (SQL):${sc.getConf.get("spark.sql.shuffle.partitions")}
      |║ GraphX Partitions:       ${sc.getConf.get("spark.graphx.numEPart", "N/A")}
      |║ Adaptive Query Enabled:  ${sc.getConf.get("spark.sql.adaptive.enabled", "false")}
      |╚════════════════════════════════════════╝
      |""".stripMargin
  }
}

