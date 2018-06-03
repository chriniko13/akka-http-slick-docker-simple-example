package com.chriniko.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.chriniko.example.db.DbManager
import com.chriniko.example.routes.{StudentRoutes, UserRoutes}

import scala.concurrent.ExecutionContextExecutor


object Main extends App {

  // --- db/slick initialization section ---
  DbManager.initStudents()

  // --- akka http initialization section ---
  implicit val system: ActorSystem = ActorSystem("main-actor-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(system))
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val userRoutes = (new UserRoutes).routes

  val studentRoutes = (new StudentRoutes).routes

  def healthCheckRoute = {
    path("healthcheck") {
      get {
        complete("Ok")
      }
    }
  }

  val allRoutes = healthCheckRoute ~ userRoutes ~ studentRoutes
  Http().bindAndHandle(allRoutes, "0.0.0.0", 5000)

  // --- db/slick termination section ---
  // DbManager.dropStudents()

}
