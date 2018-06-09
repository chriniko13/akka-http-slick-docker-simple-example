package com.chriniko.example.db

import com.chriniko.example.models.Student
import slick.jdbc.MySQLProfile.api._


class StudentsTable(tag: Tag) extends Table[Student](tag, "STUDENTS") {

  // columns
  def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
  def firstname = column[Option[String]]("FIRSTNAME", O.Length(100))
  def initials = column[Option[String]]("INITIALS", O.Length(100))
  def surname = column[Option[String]]("SURNAME", O.Length(100))

  // indexes
  def surnameIndex = index("STUDENT_SURNAME_IDX", surname, unique = false)

  // select
  def * = (id, firstname, initials, surname) <> (Student.tupled, Student.unapply)
}

