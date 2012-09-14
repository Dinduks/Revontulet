package models

import play.api.libs.ws._
import play.api.libs.json._
import play.api.libs.concurrent._

case class Repository(
  val owner:        Option[String]    = None,
  val name:         Option[String]    = None,
  val language:     Option[String]    = None,
  val description:  Option[String]    = None,
  val commits:      List[Commit]      = List(),
  val contributors: List[Contributor] = List()
) {
  def getAllInfo(githubApiUrl: String, githubGetRepoCommitsPath: String): Promise[Option[Repository]] = {
    WS.url(githubGetRepoCommitsPath.format(githubApiUrl, owner.get, name.get)).get().map { response =>
      if (response.json.toString == """{"message":"Not Found"}""") {
        None
      } else {
        val commits = Repository.parseJsonCommits(response.json.as[List[JsValue]])
        val contributors = Repository.computeContributors(commits)

        Some(Repository(this.owner, this.name, this.language, this.description, commits, contributors))
      }
    }
  }
}

object Repository {
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

  def parseJsonCommits(jsonCommits: List[JsValue]): List[Commit] = {
    jsonCommits.map { commit =>
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
  }

  def computeContributors(commits: List[Commit]): List[Contributor] = {
    var contributorsMap: Map[String, Contributor] = Map()
    for (commit <- commits; author <- commit.author) {
      val key = author.username.getOrElse(author.email.getOrElse(""))

      contributorsMap.get(key) match {
        case Some(contributor) => {
          contributor.contributionsCounter = contributor.contributionsCounter + 1
        }
        case None => {
          val contributor = Contributor(
            (commit.author.getOrElse(new User).username),
            (commit.author.getOrElse(new User).name),
            (commit.author.getOrElse(new User).email),
            (commit.author.getOrElse(new User).avatarUrl),
            1
          )

          contributorsMap += (key -> contributor)
        }
      }
    }

    contributorsMap.map{ case(k, v) => (v) }.toList
  }
}

