package com.scout24.cars.utils

import org.flywaydb.core.Flyway

//Migration using Flyway  for DB schema management
trait Migration extends Config {

  //Creates new Flyway instance with database information
  private val flyway = new Flyway()
  flyway.setDataSource(databaseUrl, databaseUser, databasePassword)

  //create car table
  def migrate(): Int = {
    flyway.migrate()
  }

  //Clean and recreate car table
  def reloadSchema(): Int = {
    flyway.clean()
    flyway.migrate()
  }

}
