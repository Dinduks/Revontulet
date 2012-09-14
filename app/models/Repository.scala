package models

import play.api.libs.ws._
import play.api.libs.json._
import play.api.libs.concurrent._

class Repository(
  val owner:        Option[String]    = None,
  val name:         Option[String]    = None,
  val language:     Option[String]    = None,
  val description:  Option[String]    = None,
  var commits:      List[Commit]      = List(),
  val contributors: List[Contributor] = List()
) {
  def getCommits(githubApiUrl: String, githubGetRepoCommitsPath: String): Promise[Repository] = {
    WS.url(githubGetRepoCommitsPath.format(githubApiUrl, owner.get, name.get)).get().map { response =>
      commits = (response.json.as[List[JsValue]]).map { commit =>
        Commit(
          (commit \ "message").asOpt[String],
          (commit \ "sha").asOpt[String],
          Some(User(
            (commit \ "author" \ "login").asOpt[String],
            (commit \ "commit" \ "author" \ "name").asOpt[String],
            (commit \ "commit" \ "author" \ "email").asOpt[String],
            (commit \ "author" \ "avatar_url").asOpt[String]
          ))
        )
      }

      computeContributors
    }
  }

  // TODO: Use getOrElse
  def computeContributors: Repository = {
    var contributorsMap: Map[String, Contributor] = Map()
    for (commit <- commits) {
      contributorsMap.get(commit.author.get.email.get) match {
        case Some(contributor) => {
          contributor.contributionsCounter = contributor.contributionsCounter + 1
        }
        case None => {
          val contributor = Contributor(
            (commit.author.get.username),
            (commit.author.get.name),
            (commit.author.get.email),
            (commit.author.get.avatarUrl),
            1
          )

          contributorsMap += (commit.author.get.email.get -> contributor)
        }
      }
    }

    Repository(owner, name, language, description, commits, contributorsMap.map{ case(k, v) => (v) }.toList)
  }
}

object Repository {
  def apply(
    owner:        Option[String]    = None,
    name:         Option[String]    = None,
    language:     Option[String]    = None,
    description:  Option[String]    = None,
    commits:      List[Commit]      = List(),
    contributors: List[Contributor] = List()
  ) = {
    new Repository(owner, name, language, description, commits, contributors)
  }

  def searchRepo(keyword: String, githubApiUrl: String, githubSearchReposPath: String): Promise[List[Repository]] = {
    WS.url(githubSearchReposPath.format(githubApiUrl, keyword)).get().map { response =>
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

