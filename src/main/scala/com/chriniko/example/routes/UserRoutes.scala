package com.chriniko.example.routes

import java.util.concurrent.locks.{ReadWriteLock, ReentrantReadWriteLock}

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpResponse, StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives.{entity, _}
import akka.http.scaladsl.server.Route
import com.chriniko.example.dto.UserDto
import com.chriniko.example.infrastructure.CustomMarshallers._
import com.chriniko.example.models.User

import scala.concurrent.Future
import scala.util.Random


class UserRoutes(implicit val system: ActorSystem) {

  import system.dispatcher

  private val lock: ReadWriteLock = new ReentrantReadWriteLock()

  private var inMemoryMap = Map(
    "1" -> User("1", "jdoe", Some("jdoe@example.com"), Some(23)),
    "2" -> User("2", "msmith", None, Some(73)),
    "3" -> User("3", "will", Some("william@example.com"), None),
    "4" -> User("4", "phil", Some("philip@example.com"), Some(20))
  )

  // Note: produce some additional records.
  {

    (1 to 20).foreach(idx => {
      inMemoryMap = inMemoryMap + (idx.toString -> {
        val record = inMemoryMap((Random.nextInt(4) + 1).toString)
        User(idx.toString, record.username, record.email, record.age)
      })
    })
  }

  // --- routes declaration ---
  def routes: Route = {

    pathPrefix("users") {

      pathEnd {
        get { // GET ALL
          complete {
            getAllUsers
          }
        }
      } ~
        post { // CREATE NEW
          entity(as[UserDto]) { userDto: UserDto =>
            complete {
              postNewUser(userDto)
            }
          }
        } ~
        path(Segment) { id =>
          get { // GET BY ID
            complete {
              getUser(id)
            }

          } ~
            delete { // DELETE BY ID
              complete {
                deleteUser(id)
              }
            } ~
            put { // UPDATE BY ID
              entity(as[UserDto]) { userDto: UserDto =>
                complete {
                  updateUser(id, userDto)
                }
              }
            }
        }
    }
  }

  // --- core methods ---
  private def deleteUser(id: String): ToResponseMarshallable = {
    writeLock {
      val res = inMemoryMap.get(id).orNull
      if (res != null) {
        inMemoryMap = inMemoryMap - res.id
        res
      } else HttpResponse(BadRequest, entity = s"does not exist user with the id: $id")
    }
  }

  private def updateUser(id: String, userDto: UserDto): ToResponseMarshallable = {
    writeLock {
      val user = inMemoryMap.get(id).orNull
      if (user == null) HttpResponse(BadRequest, entity = s"does not exist user with the id: $id")
      else {

        val updatedUser = User(
          id,
          userDto.username,
          userDto.email.orElse(user.email),
          userDto.age.orElse(user.age)
        )

        inMemoryMap = inMemoryMap - id
        inMemoryMap = inMemoryMap + (id -> updatedUser)

        (StatusCodes.OK, updatedUser)
      }
    }
  }

  private def getAllUsers: Future[(StatusCode, List[User])] = {
    Future {
      readLock {
        (StatusCodes.OK, readLock(inMemoryMap.values.toList.sortBy(u => u.id.toInt)))
      }
    }
  }

  private def getUser(id: String): ToResponseMarshallable = {
    readLock {
      val user = inMemoryMap.get(id).orNull
      if (user != null) user
      else HttpResponse(BadRequest, entity = s"does not exist user with the id: $id")
    }
  }

  private def postNewUser(userForm: UserDto) = {
    Future {
      writeLock {
        val newId = createId()
        val userModel = User(newId, userForm.username, userForm.email, userForm.age)
        inMemoryMap = inMemoryMap + (newId -> userModel)

        // a tuple where the first element is a status code will also be
        // converted (unmarshalled) to json
        (StatusCodes.Created, userModel)
      }
    }
  }

  private def createId() : String = {
    var newId = ""
    do {
      newId = Random.nextInt(100).toString
    } while (inMemoryMap.keys.exists(k => k.equals(newId)))
    newId
  }

  // --- infrastructure methods ---
  private def writeLock[T](criticalSection: => T): T = {
    lock.writeLock().lock()
    try {
      criticalSection
    } finally {
      lock.writeLock().unlock()
    }
  }

  private def readLock[T](criticalSection: => T): T = {
    lock.readLock().lock()
    try {
      criticalSection
    } finally {
      lock.readLock().unlock()
    }
  }

}
