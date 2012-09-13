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
      WS.url(githubApiUrl + githubSearchReposPath + keyword).get().map { response =>
        val repos = (response.json \ "repositories")
        Ok(views.html.searchResults(repos.as[List[JsValue]], keyword))
      }
    }
  }

  def repoInfo(username: String, repositoryName: String) = Action {
    val githubApiUrl = Play.current.configuration.getString("github.apiUrl").get

    Async {
      WS.url(githubApiUrl + "/repos/" + username + "/" + repositoryName + "/commits").get().map { response =>
        val commits = (response.json)
        val contributorsList = getSortedCommittersList(commits)
        Ok(views.html.showRepoInfo(username, repositoryName, contributorsList))
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
            Some((author \ "login").toString),
            Some((commitAuthor \ "name").toString),
            Some((commitAuthor \ "email").toString),
            Some((author \ "avatar_url").toString),
            1
          )
          committersList += ((commitAuthor \ "email").toString -> contributor)
        }
      }
    }

    committersList
  }
}

