ThisBuild / version := "1.0.0"
ThisBuild / scalaVersion := "2.12.15"
ThisBuild / organization := "com.boardgame"

lazy val root = (project in file("."))
  .settings(
    name := "board-game-pagerank",

    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "3.5.0",
      "org.apache.spark" %% "spark-sql" % "3.5.0",
      "org.apache.spark" %% "spark-graphx" % "3.5.0",
      "org.json4s" %% "json4s-native" % "4.0.6",
      "org.json4s" %% "json4s-jackson" % "4.0.6",
      "com.typesafe" % "config" % "1.4.2",
      "org.scalatest" %% "scalatest" % "3.2.15" % "test"
    ),

    assembly / mainClass := Some("com.boardgame.Main"),
    assembly / assemblyOutputPath := file("target/board-game-pagerank.jar"),

    javacOptions ++= Seq("--release", "11"),
    scalacOptions ++= Seq("-deprecation", "-feature"),

    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", "MANIFEST.MF")         => MergeStrategy.discard
      case PathList("META-INF", "INDEX.LIST")          => MergeStrategy.discard
      case PathList("META-INF", "DEPENDENCIES")        => MergeStrategy.discard
      case PathList("META-INF", "NOTICE")              => MergeStrategy.discard
      case PathList("META-INF", "LICENSE")             => MergeStrategy.discard
      case PathList("META-INF", "NOTICE.txt")          => MergeStrategy.discard
      case PathList("META-INF", "LICENSE.txt")         => MergeStrategy.discard

      
      case PathList("META-INF", file) if file.toLowerCase.endsWith(".sf")  => MergeStrategy.discard
      case PathList("META-INF", file) if file.toLowerCase.endsWith(".dsa") => MergeStrategy.discard
      case PathList("META-INF", file) if file.toLowerCase.endsWith(".rsa") => MergeStrategy.discard

      case PathList("META-INF", "services", _ @ _*)   => MergeStrategy.filterDistinctLines

      // Não mexer em META-INF/versions
      case PathList("META-INF", "versions", _ @ _*)   => MergeStrategy.first

      // Ignorar module-info
      case PathList(xs @ _*) if xs.last == "module-info.class" => MergeStrategy.first

      // Netty conflicts
      case PathList("META-INF", file) if file.toLowerCase.startsWith("io.netty.versions") =>
        MergeStrategy.first

      // Outros metadados
      case PathList("google", "protobuf", _ @ _*) => MergeStrategy.first
      case PathList("arrow-git.properties")       => MergeStrategy.first
      case PathList("log4j2plugins.dat")          => MergeStrategy.first

      case _ => MergeStrategy.first
    }
  )


