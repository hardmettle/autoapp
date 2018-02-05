package com.scout24.cars.services.dao

import com.scout24.cars.models.{ CarIdentification, CarRegistration, Fuel }
import com.scout24.cars.services.dao.CarsDaoDefinitions.CarsDaoImpl
import com.scout24.cars.utils.ActorContext
import slick.lifted.ProvenShape

import scala.concurrent.Future

//Trait that represent DAO for Car
private[services] trait CarsDao {
  /**
   * Takes CarRegistration and saves/create new car into Car table
   * @param carRegistration
   * @return Unit
   */
  def create(carRegistration: CarRegistration): Future[Unit]

  /**
   * Reads Car from car table represented by CarIdentification
   *
   * @param carIdentification
   * @return Option of CarRegistration
   */
  def read(carIdentification: CarIdentification): Future[Option[CarRegistration]]

  /**
   * Reads all cars from car table
   * @return sequence of CarRegistration
   */
  def readAll(): Future[Seq[CarRegistration]]

  /**
   * Updates current car in table with new CarRegistration
   * @param carRegistration
   * @return Unit
   */
  def update(carRegistration: CarRegistration): Future[Unit]

  /**
   * Deletes Car from car table represented by CarIdentification
   * @param carIdentification
   * @return Unit
   */
  def delete(carIdentification: CarIdentification): Future[Unit]
}
//Companion object to instantiate CarsDaoImpl
object CarsDao {
  def apply(): CarsDao = new CarsDaoImpl()
}
//car dao object that concrete definition to dao methods eg: read write etc.
//Also encapsulates CarTable definition representing actual car table
private[dao] object CarsDaoDefinitions extends SlickDAO[CarRegistration, CarRegistration] {

  import dbProfile.profile.api._

  override lazy val query: TableQuery[CarsTable] = TableQuery[CarsTable]

  /*override def toRow(domainObject: CarRegistration): CarRegistration = ???

  override def fromRow(dbRow: CarRegistration): CarRegistration = ???*/

  // Represents the actual car table. Defines field/columns with
  // attributes of table and also the serialization/deserialzation of case class to row and vice versa
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

  // Actual implementation trait CarsDao representing DAO for car table
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
