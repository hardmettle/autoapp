package com.scout24.cars.services.dao

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

//SlickDao trait to be mixed in with CarsDao
trait SlickDAO[DbRow, DomainObject] {
  //Instantiates database profile after reading from config file
  protected val dbProfile: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("db.cars")
  //Database of database profile
  protected val db = dbProfile.db
  import dbProfile.profile.api._

  /**
   * Represents database car table. Profiles add extension methods to TableQuery
   * for operations that can be performed on tables but not on arbitrary
   * queries, e.g. getting the table DDL
   */
  val query: TableQuery[_ <: Table[DbRow]]
}

