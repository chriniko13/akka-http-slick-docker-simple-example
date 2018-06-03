package com.chriniko.example.models

case class Address(id: Option[String],
                   userId: String,
                   addressLine: String,
                   city: String,
                   postalCode: String)