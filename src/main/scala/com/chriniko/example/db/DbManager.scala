package com.chriniko.example.db

import java.util.concurrent.Executors

import com.chriniko.example.dao.StudentsDao
import slick.dbio.DBIOAction

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object DbManager extends DbConfiguration {

  private implicit val ex: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(2))

  def initStudents(): Unit = {

    println(s"initStudents statements: ${StudentsDao.schemaCreation().statements}")

    db.run(DBIOAction.seq(StudentsDao.schemaCreation()))
      .onComplete(res => {

        if (res.isSuccess)
          println("initStudents success")
        else {
          println(s"initStudents failure --- error: ${res.failed.toString}")
        }

      })
  }

  def dropStudents(): Unit = {

    println(s"dropStudents statements: ${StudentsDao.schemaDeletion().statements}")

    db.run(DBIOAction.seq(StudentsDao.schemaDeletion()))
      .onComplete(res => {

        if (res.isSuccess)
          println("dropStudents success")
        else {
          println(s"dropStudents failure --- error: ${res.failed.toString}")
        }

      })
  }
}

