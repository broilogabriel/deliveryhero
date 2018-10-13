package com.deliveryhero.restaurant.service

import java.io._
import java.nio.charset.StandardCharsets

import com.deliveryhero.restaurant.IdProvider
import com.deliveryhero.restaurant.model.Restaurant
import com.typesafe.scalalogging.LazyLogging
import org.json4s.Formats
import org.json4s.jackson.Serialization.{read, writePretty}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class RestaurantService(filePath: Option[String] = None)
                       (implicit formats: Formats,
                        idProvider: IdProvider[Long]) extends Service[Long, Restaurant] with LazyLogging {


  override def create(obj: Restaurant): Future[Option[Long]] = Future {
    val nextId = idProvider.getNextId
    save(safeLoad :+ obj.copy(id = Some(nextId)))
    Some(nextId)
  }

  override def get: Future[Seq[Restaurant]] = Future {
    safeLoad
  }

  private def safeLoad = {
    filePath.map(fromFile) getOrElse saveEmptyFile
  }

  override def getById(id: Long): Future[Option[Restaurant]] = ???

  override def update(id: Long): Future[String] = ???

  override def delete(id: Long): Future[String] = ???

  private def fromFile(filePath: String): Seq[Restaurant] = Try {
    if (!new File(filePath).exists()) {
      logger.info(s"File $filePath does not exist, defaulting to empty file")
      saveEmptyFile
    }
    read[Seq[Restaurant]](new InputStreamReader(new FileInputStream(filePath), "utf-8"))
  } match {
    case Success(configuration) => configuration
    case Failure(t: Throwable) => {
      logger.error(s"Unable to read configuration from $filePath", t)
      saveEmptyFile
    }
  }

  private def saveEmptyFile = {
    val currentRestaurants = read[Seq[Restaurant]]("[]")
    save(currentRestaurants)
    currentRestaurants
  }

  def save(restaurants: Seq[Restaurant]): Unit = filePath.map(path => {
    val json = writePretty[Seq[Restaurant]](restaurants)
    val writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(path)), StandardCharsets.UTF_8), true)
    try {
      writer.write(json)
    } finally {
      writer.close()
    }
  })
}
