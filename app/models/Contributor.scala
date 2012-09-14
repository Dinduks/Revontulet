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
    val url = username match {
      case None => None
      case Some(username) => Some("https://github.com/%s" format Util.trim(username, '"'))
    }

    new Contributor(
      username,
      name,
      email,
      url,
      avatarUrl,
      contributionsCounter
    )
  }
}
