package com.boardgame.models

import scala.collection.Seq

case class BoardGame(
  id: Long,
  title: String,
  year: Int,
  rank: Int,
  minplayers: Int,
  maxplayers: Int,
  minplaytime: Int,
  maxplaytime: Int,
  minage: Int,
  rating: GameRating,
  recommendations: Seq[Long],
  types: GameTypes
) {
  def avgPlayTime: Double = (minplaytime + maxplaytime) / 2.0
  def playerRange: String = s"$minplayers-$maxplayers"
  def recommendationCount: Int = recommendations.length
}

object BoardGame {
  def empty: BoardGame = {
    BoardGame(
      id = 0L,
      title = "Unknown",
      year = 0,
      rank = 0,
      minplayers = 0,
      maxplayers = 0,
      minplaytime = 0,
      maxplaytime = 0,
      minage = 0,
      rating = GameRating(0.0, 0L),
      recommendations = Seq(),
      types = GameTypes(Seq(), Seq())
    )
  }
}
