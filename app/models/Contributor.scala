package models

import revontulet.Util

case class Contributor(
  override val username:  Option[String] = None,
  override val name:      Option[String] = None,
  override val email:     Option[String] = None,
  override val avatarUrl: Option[String] = None,
  var contributionsCounter: Int = 0
) extends User(
  username,
  name,
  email,
  avatarUrl
)

