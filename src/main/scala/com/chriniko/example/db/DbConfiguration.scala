package com.chriniko.example.db

import slick.basic.DatabaseConfig
import slick.jdbc.{JdbcBackend, MySQLProfile}

trait DbConfiguration {
  val db: JdbcBackend#DatabaseDef = {
    val config = DatabaseConfig.forConfig[MySQLProfile]("mysql")

    //    println(config.config.toString)
    //    println(config.profileName)
    //    println(config.profile.api)
    //    println(config.db.source.toString)

    config.db
  }
}
