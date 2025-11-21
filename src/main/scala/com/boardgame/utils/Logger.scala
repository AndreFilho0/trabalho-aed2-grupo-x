package com.boardgame.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logger {
  
  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  
  def info(message: String): Unit = {
    println(s"[INFO] [${LocalDateTime.now.format(formatter)}] $message")
  }
  
  def success(message: String): Unit = {
    println(s"[✓] [${LocalDateTime.now.format(formatter)}] $message")
  }
  
  def error(message: String): Unit = {
    println(s"[ERROR] [${LocalDateTime.now.format(formatter)}] $message")
  }
  
  def warning(message: String): Unit = {
    println(s"[WARN] [${LocalDateTime.now.format(formatter)}] $message")
  }
}
