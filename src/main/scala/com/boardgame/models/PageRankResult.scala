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
      case x if x > 0.015 => "Muito Alta"
      case x if x > 0.010 => "Alta"
      case x if x > 0.005 => "Média"
      case _ => "Baixa"
    }
  }
}
