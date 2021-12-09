package example

import org.scalamock.scalatest.MockFactory
import org.scalatest.EitherValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AddressValidationServiceSpec extends AnyFreeSpec with Matchers with ScalaFutures with MockFactory with EitherValues {

  "AddressValidationService" - {

    val mockConnector: AddressValidationConnector = stub[AddressValidationConnector]
    val service: AddressValidationService = new AddressValidationService(mockConnector)

    "validateAddress" - {

      "must return an address when given valid input" in {
        (mockConnector.validate _).when(*).onCall((a: Address) => Future.successful(Right(a)))
        val result = service.validateAddress(AddressRequest(List("foo"), "bar")).futureValue.value
        result mustEqual Address(List("foo"), "bar")
      }

      "must return an error message if the lines are empty" in {
        val result = service.validateAddress(AddressRequest(List.empty, "bar")).futureValue.left.value
        result.length mustEqual 1
        result.head mustEqual "Lines were empty"
      }

      "must return an error messages if individual lines are invalid" in {
        val result = service.validateAddress(AddressRequest(List("", "a" * 101), "bar")).futureValue.left.value
        result.length mustEqual 2
        result.toList must contain only inOrder(
          "(Line: 0) Address line is empty",
          "(Line: 1) Address line is longer than 100 characters"
        )
      }

      "must return an error message if the postcode is empty" in {
        val result = service.validateAddress(AddressRequest(List("foo"), "")).futureValue.left.value
        result.length mustEqual 1
        result.head mustEqual "Post code is empty"
      }

      "must return all errors" in {
        val result = service.validateAddress(AddressRequest(List.empty, "")).futureValue.left.value
        result.length mustEqual 2
        result.toList must contain only inOrder(
          "Lines were empty",
          "Post code is empty"
        )
      }

      "must return an error message if the address cannot be validated by the connector" in {
        (mockConnector.validate _).when(*).returns(Future.successful(Left(List("Invalid!"))))
        val result = service.validateAddress(AddressRequest(List("foo"), "bar")).futureValue.left.value
        result.length mustEqual 1
        result.head mustEqual "Invalid!"
      }

      "must return a failed future if the connector fails" in {
        (mockConnector.validate _).when(*).returns(Future.failed(new Exception("boom")))
        val exception = service.validateAddress(AddressRequest(List("foo"), "bar")).failed.futureValue
        exception.getMessage mustEqual "boom"
      }
    }

    "validateAddresses" - {

      "must return valid addresses when requests are valid" in {
        (mockConnector.validate _).when(*).onCall((a: Address) => Future.successful(Right(a)))
        val requests = List(
          AddressRequest(List("foo"), "bar"),
          AddressRequest(List("baz"), "quux")
        )
        val result = service.validateAddresses(requests).futureValue.value
        result must contain only inOrder(
          Address(List("foo"), "bar"),
          Address(List("baz"), "quux")
        )
      }

      "must return an error message if the lines are empty" in {
        val result = service.validateAddresses(List(AddressRequest(List.empty, "bar"))).futureValue.left.value
        result.length mustEqual 1
        result.head mustEqual "(Request: 0) Lines were empty"
      }

      "must return an error messages if individual lines are invalid" in {
        val result = service.validateAddresses(List(AddressRequest(List("", "a" * 101), "bar"))).futureValue.left.value
        result.length mustEqual 2
        result must contain only inOrder(
          "(Request: 0) (Line: 0) Address line is empty",
          "(Request: 0) (Line: 1) Address line is longer than 100 characters"
        )
      }

      "must return an error message if the postcode is empty" in {
        val result = service.validateAddresses(List(AddressRequest(List("foo"), ""))).futureValue.left.value
        result.length mustEqual 1
        result.head mustEqual "(Request: 0) Post code is empty"
      }

      "must return all errors" in {
        val result = service.validateAddresses(List(AddressRequest(List.empty, ""))).futureValue.left.value
        result.length mustEqual 2
        result.toList must contain only inOrder(
          "(Request: 0) Lines were empty",
          "(Request: 0) Post code is empty"
        )
      }

      "must return the errors from all invalid addresses" in {
        val requests = List(
          AddressRequest(List.empty, ""),
          AddressRequest(List.empty, "")
        )
        val result = service.validateAddresses(requests).futureValue.left.value
        result.length mustEqual 4
        result must contain inOrder (
          "(Request: 0) Lines were empty",
          "(Request: 0) Post code is empty",
          "(Request: 1) Lines were empty",
          "(Request: 1) Post code is empty"
        )
      }

      "must return a failed future if the connector fails" in {
        (mockConnector.validate _).when(*)
          .returns(Future.successful(Right(Address(List("foo"), "bar"))))
          .returns(Future.failed(new Exception("boom")))
        val requests = List(
          AddressRequest(List("foo"), "bar"),
          AddressRequest(List("baz"), "quux")
        )
        val exception = service.validateAddresses(requests).failed.futureValue
        exception.getMessage mustEqual "boom"
      }
    }
  }
}
