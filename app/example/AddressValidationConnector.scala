package example

import cats.data.NonEmptyList

import scala.concurrent.Future

abstract class AddressValidationConnector {
  def validate(address: Address): Future[Either[NonEmptyList[String], Address]]
}
