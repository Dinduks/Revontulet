package models

case class User(
  username:  Option[String] = None,
  name:      Option[String] = None,
  email:     Option[String] = None,
  url:       Option[String] = None,
  avatarUrl: Option[String] = None
)

