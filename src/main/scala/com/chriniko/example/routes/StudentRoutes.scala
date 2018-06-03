package com.chriniko.example.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{as, complete, delete, entity, get, path, pathEnd, pathPrefix, post, put, _}
import akka.http.scaladsl.server.Route
import com.chriniko.example.dao.StudentsDao
import com.chriniko.example.dto.StudentDto
import com.chriniko.example.infrastructure.CustomMarshallers._
import com.chriniko.example.models.Student

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class StudentRoutes(implicit val system: ActorSystem) {

  // Note: add some records.
  {
    (1 to 10).foreach(idx => {
      StudentsDao.insert(Student(None, Some(s"first-$idx"), Some(s"initials-$idx"), Some(s"surname-$idx")))
    })
  }

  // --- routes declaration ---
  def routes: Route = {

    path("surnames") {
      get {
        complete {
          val res = Await.result(StudentsDao.getAllSurnames(), Duration.Inf)
          res.distinct
        }
      }
    } ~ pathPrefix("students") {

      pathEnd {
        get { // GET ALL
          complete {
            ToResponseMarshallable(StudentsDao.findAll())
          }
        }
      } ~
        post { // CREATE NEW
          entity(as[StudentDto]) { studentDto: StudentDto =>
            complete {
              StudentsDao.insert(Student(None, studentDto.firstname, studentDto.initials, studentDto.surname))
              (StatusCodes.Created, studentDto)
            }
          }
        } ~
        path(Segment) { id =>
          get { // GET BY ID
            complete {
              val res = Await.result(StudentsDao.findById(id.toLong), Duration.Inf)
              res match {
                case Some(s) => (StatusCodes.OK, s)
                case None => HttpResponse(StatusCodes.BadRequest, entity = s"does not exist student with the id: $id")
              }
            }

          } ~
            delete { // DELETE BY ID
              complete {
                val res = Await.result(StudentsDao.deleteById(id.toLong), Duration.Inf)
                if (res) {
                  StatusCodes.OK
                } else {
                  HttpResponse(StatusCodes.BadRequest, entity = s"does not exist student with the id: $id")
                }
              }
            } ~
            put { // UPDATE BY ID
              entity(as[StudentDto]) { studentDto: StudentDto =>
                complete {
                  val res = Await.result(
                    StudentsDao.update(id.toLong, (studentDto.firstname, studentDto.initials, studentDto.surname)),
                    Duration.Inf
                  )

                  if (res) {
                    StatusCodes.OK
                  } else {
                    HttpResponse(StatusCodes.BadRequest, entity = s"does not exist student with the id: $id")
                  }
                }
              }
            }
        }
    }

  }

}
