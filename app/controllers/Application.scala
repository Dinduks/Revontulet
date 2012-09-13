package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws._
import play.api.libs.json._

import models._

object Application extends Controller {
  def index = Action {
    Ok(views.html.index())
  }

  def search(keyword: String) = Action {
    val githubApiUrl = Play.current.configuration.getString("github.apiUrl").get
    val githubSearchReposPath = Play.current.configuration.getString("github.searchReposPath").get

    Async {
      Repository.searchRepo(keyword, githubApiUrl, githubSearchReposPath).map { repos =>
        Ok(views.html.searchResults(repos, keyword))
      }
    }
  }

  def repoInfo(username: String, repositoryName: String) = Action {
    val githubApiUrl = Play.current.configuration.getString("github.apiUrl").get
    val githubGetRepoCommitsPath = Play.current.configuration.getString("github.getRepoCommitsPath").get

    Async {
      WS.url(githubGetRepoCommitsPath.format(githubApiUrl, username, repositoryName)).get().map { response =>
        val commits = (response.json)
        val commitsCount = commits.as[List[JsValue]].length
        val contributorsList = getSortedCommittersList(commits)
        Ok(views.html.showRepoInfo(username, repositoryName, contributorsList, commitsCount))
      }
    }
  }

  def getSortedCommittersList(commits: JsValue): Map[String, Contributor] = {
    val commitsList = commits.as[List[JsValue]]
    var committersList: Map[String, Contributor] = Map()

    for(commit <- commitsList) {
      committersList.get((commit \ "commit" \ "author" \ "email").toString) match {
        case Some(committer) => {
          committer.contributionsCounter = committer.contributionsCounter + 1
        }
        case None => {
          val commitAuthor = commit \ "commit" \ "author"
          val author       = commit \ "author"
          val contributor = Contributor(
            (author \ "login").asOpt[String],
            (commitAuthor \ "name").asOpt[String],
            (commitAuthor \ "email").asOpt[String],
            (author \ "avatar_url").asOpt[String],
            1
          )

          committersList += ((commitAuthor \ "email").toString -> contributor)
        }
      }
    }

    committersList
  }
}

