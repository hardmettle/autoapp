package com.scout24.cars.services.dao

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

trait SlickDAO[DbRow, DomainObject] {
  protected val dbProfile: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("db.cars")
  protected val db = dbProfile.db
  import dbProfile.profile.api._

  val query: TableQuery[_ <: Table[DbRow]]
}

