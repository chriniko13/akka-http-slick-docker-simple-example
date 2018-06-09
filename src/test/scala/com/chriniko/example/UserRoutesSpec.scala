package com.chriniko.example

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.chriniko.example.dto.UserDto
import com.chriniko.example.models.User
import com.chriniko.example.routes.UserRoutes
import org.scalatest.{Matchers, WordSpec}
import com.chriniko.example.infrastructure.CustomMarshallers._

// Note: Akka Http Testkit.
class UserRoutesSpec extends WordSpec with Matchers with ScalatestRouteTest {

  val route: Route = new UserRoutes().routes

  "The user route " should {

    " get requests work as expected --- good case" in {

      Get("/users") ~> route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[List[User]].size shouldBe 20
      }

      Get("/users/1") ~> route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[User].id shouldBe "1"
      }

      Get("/users/999") ~> route ~> check {
        status shouldEqual StatusCodes.BadRequest
      }
    }


    "delete request work as expected --- good case" in {

      Delete("/users/1") ~> route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[User].id shouldBe "1"
      }

    }

    "delete request work as expected when record does not exist --- bad case" in {

      Delete("/users/1") ~> route ~> check {
        status shouldEqual StatusCodes.BadRequest
        responseAs[String] shouldBe "does not exist user with the id: 1"
      }

    }

    "post request work as expected --- good case" in {

      Post("/users", UserDto("some_user", Some("email@mail.gr"), Some(26))) ~> route ~> check {
        status shouldEqual StatusCodes.Created
        responseAs[User].username shouldBe "some_user"
        responseAs[User].email.get shouldBe "email@mail.gr"
        responseAs[User].age.get shouldBe 26

      }

    }

    "put request work as expected --- good case" in {

      Put("/users/12", UserDto("some_user", Some("email@mail.gr"), Some(26))) ~> route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[User].username shouldBe "some_user"
        responseAs[User].email.get shouldBe "email@mail.gr"
        responseAs[User].age.get shouldBe 26

        Get("/users/12") ~> route ~> check {
          responseAs[User].id shouldBe "12"
          responseAs[User].username shouldBe "some_user"
          responseAs[User].email.get shouldBe "email@mail.gr"
          responseAs[User].age.get shouldBe 26
        }

      }

    }

  }

}
