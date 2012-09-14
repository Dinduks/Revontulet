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

    val repo = new Repository(Some(username), Some(repositoryName), None, None)
    Async {
      repo.getAllInfo(githubApiUrl, githubGetRepoCommitsPath).map { repo =>
        Ok(views.html.showRepoInfo(repo))
      }
    }
  }
}

