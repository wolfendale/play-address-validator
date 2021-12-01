package example

import cats.data._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressValidationService @Inject()(
                                          connector: AddressValidationConnector
                                        )(implicit ec: ExecutionContext) {

  def validateAddresses(requests: List[AddressRequest]): Future[Either[NonEmptyList[String], List[Address]]] = ???

  def validateAddress(request: AddressRequest): Future[Either[NonEmptyList[String], Address]] = ???
}
