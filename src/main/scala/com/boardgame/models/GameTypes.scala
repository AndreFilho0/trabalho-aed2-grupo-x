package com.boardgame.models

case class Category(id: Long, name: String)
case class Mechanic(id: Long, name: String)

case class GameTypes(
  categories: Seq[Category],
  mechanics: Seq[Mechanic]
)
