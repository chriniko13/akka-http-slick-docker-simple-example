package com.chriniko.example

import com.chriniko.example.dao.StudentsDao
import com.chriniko.example.db.DbConfiguration
import com.chriniko.example.models.Student
import org.specs2.matcher.{FutureMatchers, OptionMatchers}
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterEach

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class StudentsDaoSpec extends Specification
  with DbConfiguration
  with FutureMatchers
  with OptionMatchers
  with BeforeAfterEach {

  sequential

  val timeout: FiniteDuration = 500 milliseconds

  val students: StudentsDao.type = StudentsDao

  override protected def before: Unit = {
    Await.result(students.init, timeout)
  }

  override protected def after: Unit = {
    Await.result(students.drop, timeout)
  }


  "User should be inserted successfully" >> {

    //given
    val student = Student(Some(1), Some("firstname"), Some("initials"), Some("surname"))

    //when
    val result = get(students.insert(student))

    //then
    result === student
  }

  "Delete operation works as expected" >> {

    //given
    val student = Student(Some(1), Some("firstname"), Some("initials"), Some("surname"))
    get(students.insert(student))

    //when
    val result = get(students.deleteById(1))

    //then
    result === true

  }

  private def get[T](f: Future[T]): T = Await.result(f, timeout)


}