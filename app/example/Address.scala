package example

import cats.data.NonEmptyList

final case class Address(lines: NonEmptyList[String], postCode: String)
