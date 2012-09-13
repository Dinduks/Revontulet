package models

import play.api.libs.ws._
import play.api.libs.json._
import play.api.libs.concurrent._

class Repository(
  val owner:        Option[String] = None,
  val name:         Option[String] = None,
  val language:     Option[String] = None,
  val description:  Option[String] = None,
  val commits:      Option[Commit] = None,
  val contributors: Option[Contributor] = None
)

object Repository {
  def apply(
    owner:        Option[String] = None,
    name:         Option[String] = None,
    language:     Option[String] = None,
    description:  Option[String] = None
  ) = {
    new Repository(owner, name, language, description, None, None)
  }

  def searchRepo(keyword: String, githubApiUrl: String, githubSearchReposPath: String): Promise[List[Repository]] = {
    WS.url(githubSearchReposPath.format(githubApiUrl, keyword)).get().map { response =>
      val json = response.json \ "repositories"
      val owner       = json \ "owner"
      val name        = json \ "name"
      val language    = json \ "language"
      val description = json \ "description"

      (response.json \ "repositories").as[List[JsValue]].map { repo =>
        Repository(
          (repo \ "owner").asOpt[String],
          (repo \ "name").asOpt[String],
          (repo \ "language").asOpt[String],
          (repo \ "description").asOpt[String]
        )
      }
    }
  }
}

