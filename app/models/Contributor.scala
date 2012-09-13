package models

import revontulet.Util

class Contributor(
  username:  Option[String] = None,
  name:      Option[String] = None,
  email:     Option[String] = None,
  url:       Option[String] = None,
  avatarUrl: Option[String] = None,
  var contributionsCounter: Int = 0
) extends User(
  username,
  name,
  email,
  url,
  avatarUrl
) {
}

object Contributor {
  def apply(
    username:  Option[String],
    name:      Option[String],
    email:     Option[String],
    avatarUrl: Option[String],
    contributionsCounter: Int
  ) = {
    new Contributor(
      username,
      name,
      email,
      Some("https://github.com/" + Util.trim(username.get, '"')),
      (avatarUrl),
      contributionsCounter
    )
  }
}
