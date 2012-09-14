package models

import revontulet._

class User(
  val username:  Option[String] = None,
  val name:      Option[String] = None,
  val email:     Option[String] = None,
  val url:       Option[String] = None,
  val avatarUrl: Option[String] = None
)

object User {
  def apply(
    username:  Option[String] = None,
    name:      Option[String] = None,
    email:     Option[String] = None,
    url:       Option[String] = None,
    avatarUrl: Option[String] = None
  ) = {
    val url = username match {
      case None => None
      case Some(username) => Some("https://github.com/%s" format Util.trim(username, '"'))
    }

    new User(username, name, email, url, avatarUrl)
  }
}

