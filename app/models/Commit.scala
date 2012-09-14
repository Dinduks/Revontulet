package models

case class Commit(
  val message: Option[String] = None,
  val sha:     Option[String] = None,
  val author:  Option[User]   = None
)

