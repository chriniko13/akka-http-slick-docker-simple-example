package com.chriniko.example.models

case class Student(id: Option[Long],
                   firstname: Option[String],
                   initials: Option[String],
                   surname: Option[String])
