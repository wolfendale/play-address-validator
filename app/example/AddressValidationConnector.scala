package example

import scala.concurrent.Future

abstract class AddressValidationConnector {
  def validate(address: Address): Future[Either[List[String], Address]]
}
