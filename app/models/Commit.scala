package models

class Commit(
  val message: Option[String] = None,
  val sha:     Option[String] = None,
  val author:  Option[User]   = None
)

object Commit {
  def apply(
    message: Option[String] = None,
    sha:     Option[String] = None,
    author:  Option[User]   = None
  ) = {
    new Commit(message, sha, author)
  }
}

