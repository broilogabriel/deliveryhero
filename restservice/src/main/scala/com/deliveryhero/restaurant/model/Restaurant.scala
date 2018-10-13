package com.deliveryhero.restaurant.model

case class Restaurant(id: Option[Long],
                      name: String,
                      phoneNo: String,
                      cuisines: Seq[String],
                      address: Address,
                      description: String)