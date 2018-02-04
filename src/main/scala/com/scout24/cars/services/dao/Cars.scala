package com.scout24.cars.services.dao

import com.scout24.cars.models.{ CarIdentification, CarRegistration, Fuel }
import com.scout24.cars.services.dao.CarsDaoDefinitions.CarsDaoImpl
import com.scout24.cars.utils.ActorContext
import slick.lifted.ProvenShape

import scala.concurrent.Future

private[services] trait CarsDao {

  def create(carRegistration: CarRegistration): Future[Unit]

  def read(carIdentification: CarIdentification): Future[Option[CarRegistration]]

  def readAll(): Future[Seq[CarRegistration]]

  def update(carRegistration: CarRegistration): Future[Unit]

  def delete(carIdentification: CarIdentification): Future[Unit]
}

object CarsDao {
  def apply(): CarsDao = new CarsDaoImpl()
}

private[dao] object CarsDaoDefinitions extends SlickDAO[CarRegistration, CarRegistration] {

  import dbProfile.profile.api._

  override lazy val query: TableQuery[CarsTable] = TableQuery[CarsTable]

  /*override def toRow(domainObject: CarRegistration): CarRegistration = ???

  override def fromRow(dbRow: CarRegistration): CarRegistration = ???*/

  class CarsTable(tag: Tag) extends Table[CarRegistration](tag, "cars") {
    override def * : ProvenShape[CarRegistration] =
      (id, title, (name, renewable), price, used, mileage, registration).shaped <> ({
        case (id, title, fuel, price, used, mileage, registration) =>
          CarRegistration(id, title, Fuel.tupled.apply(fuel), price, used, mileage, registration)
      }, { c: CarRegistration =>
        def f(fuel: Fuel) = Fuel.unapply(fuel).get

        Some(c.id, c.title, f(c.fuel), c.price, c.used, c.mileage, c.registration)
      })

    def id = column[Long]("id")

    def title = column[String]("title")

    def name = column[String]("fuel_name")

    def renewable = column[Boolean]("fuel_renewable")

    def price = column[Int]("price")

    def used = column[Boolean]("used")

    def mileage = column[Option[Int]]("mileage")

    def registration = column[Option[String]]("registration")

    def pk = primaryKey("cars_pk", id)

  }

  class CarsDaoImpl extends CarsDao with ActorContext {
    override def create(carRegistration: CarRegistration): Future[Unit] = {
      db.run(query += carRegistration).map(_ => ())
    }

    override def read(carIdentification: CarIdentification): Future[Option[CarRegistration]] = {
      db.run(query.filter(e => e.id === carIdentification.id).result)
        .map(_.headOption)
    }
    override def readAll(): Future[Seq[CarRegistration]] = {
      db.run(query.result)
    }
    override def update(carRegistration: CarRegistration): Future[Unit] = {
      db.run(
        query.filter(e => e.id === carRegistration.id).update(carRegistration)).map(_ => ())
    }

    override def delete(carIdentification: CarIdentification): Future[Unit] = {
      db.run(query.filter(e => e.id === carIdentification.id).delete)
        .map(_ => ())
    }
  }

}
