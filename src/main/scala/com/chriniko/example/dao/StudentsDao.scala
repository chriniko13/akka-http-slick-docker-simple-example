package com.chriniko.example.dao

import com.chriniko.example.db.{DbConfiguration, StudentsTable}
import com.chriniko.example.models.Student
import slick.dbio.Effect
import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery
import slick.sql.FixedSqlAction

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

object StudentsDao extends TableQuery(new StudentsTable(_)) with DbConfiguration {

  private implicit val executor: ExecutionContextExecutor = ExecutionContext.Implicits.global

  def schemaCreation(): FixedSqlAction[Unit, NoStream, Effect.Schema] = this.schema.create

  def schemaDeletion(): FixedSqlAction[Unit, NoStream, Effect.Schema] = this.schema.drop

  def init: Future[Unit] = {
    db.run(schemaCreation())
  }

  def drop: Future[Unit] = {
    db.run(schemaDeletion())
  }

  def findById(id: Long): Future[Option[Student]] = {
    db.run(this.filter(st => st.id === id).result).map(s => s.headOption)
  }

  def deleteById(id: Long): Future[Boolean] = {
    db.run(this.filter(_.id === id).delete.map(_ > 0))
  }

  def insert(student: Student): Future[Student] = {
    db.run(this returning this.map(_.id) += student)
      .map(id => student.copy(id = id))
  }

  def findAll(): Future[Seq[Student]] = {
    db.run((for (user <- this) yield user).result)
  }

  def update(id: Long, toUpdate: (Option[String], Option[String], Option[String])): Future[Boolean] = {
    db.run(
      this.filter(_.id === id)
        .map(rec => (rec.firstname, rec.initials, rec.surname))
        .update(toUpdate)
        .map(_ > 0)
    )
  }

  def getAllSurnames: Future[Vector[String]] = {
    db.run(
      sql"SELECT SURNAME FROM test_slick.STUDENTS".as[String]
    )
  }

}
