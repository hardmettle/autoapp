package com.scout24.cars.utils

import com.typesafe.config.ConfigFactory

// Loads all the config required throughout the application
trait Config {
  private val config = ConfigFactory.load()
  private val httpConfig = config.getConfig("http")
  private val databaseConfig = config.getConfig("db.cars")

  val httpInterface = httpConfig.getString("interface")
  val httpPort = httpConfig.getInt("port")

  val databaseUrl = databaseConfig.getString("db.url")
  val databaseUser = databaseConfig.getString("db.user")
  val databasePassword = databaseConfig.getString("db.password")
}
