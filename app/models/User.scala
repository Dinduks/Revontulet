package models

import revontulet._

class User(
  val username:  Option[String] = None,
  val name:      Option[String] = None,
  val email:     Option[String] = None,
  val avatarUrl: Option[String] = None
) {
  lazy val url = username.map (u => "https://github.com/%s" format Util.trim(u, '"'))
}

object User {
  def apply(
    username:  Option[String] = None,
    name:      Option[String] = None,
    email:     Option[String] = None,
    avatarUrl: Option[String] = None
  ) = {
    new User(username, name, email, avatarUrl)
  }
}

