package com.chriniko.example.infrastructure

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.chriniko.example.dto.{StudentDto, UserDto}
import com.chriniko.example.models.{Student, User}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object CustomMarshallers extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val userFormat: RootJsonFormat[User] = jsonFormat4(User)
  implicit val userDtoFormat: RootJsonFormat[UserDto] = jsonFormat3(UserDto)

  implicit val studentFormat: RootJsonFormat[Student] = jsonFormat4(Student)
  implicit val studentDtoFormat: RootJsonFormat[StudentDto] = jsonFormat3(StudentDto)
}
