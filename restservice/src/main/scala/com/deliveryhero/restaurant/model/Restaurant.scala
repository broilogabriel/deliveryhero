package com.deliveryhero.restaurant.model

case class Restaurant(name: String, phoneNo: String, cuisines: Seq[String], address: Address, description: String)