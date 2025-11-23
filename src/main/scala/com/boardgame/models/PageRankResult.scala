package com.boardgame.models

case class PageRankResult(
  gameId: Long,
  gameTitle: String,
  rank: Int,
  pageRankScore: Double,
  rating: Double,
  year: Int,
  recommendationCount: Int
) {
  def influence: String = {
    pageRankScore match {
      case x if x > 1.5 => "Muito Alta"
      case x if x > 1.0 => "Alta"
      case x if x > 0.5 => "Média"
      case _ => "Baixa"
    }
  }
}
