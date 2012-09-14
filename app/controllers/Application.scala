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

  def getGitHubApiConfig: (String, String, String) = {
    val githubApiUrl = Play.current.configuration.getString("github.apiUrl").get
    val githubSearchReposPath = Play.current.configuration.getString("github.searchReposPath").get
    val githubGetRepoCommitsPath = Play.current.configuration.getString("github.getRepoCommitsPath").get

    (githubApiUrl, githubSearchReposPath, githubGetRepoCommitsPath)
  }

  def search(keyword: String) = Action {
    val (githubApiUrl, githubSearchReposPath, githubGetRepoCommitsPath) = getGitHubApiConfig

    Async {
      Repository.searchRepo(keyword, githubApiUrl, githubSearchReposPath).map { repos =>
        repos match {
          case Nil => NotFound(views.html.searchResultsNotFound(keyword))
          case _   => Ok(views.html.searchResultsOk(repos, keyword))
        }
      }
    }
  }

  def repoInfo(username: String, repositoryName: String) = Action {
    val (githubApiUrl, githubSearchReposPath, githubGetRepoCommitsPath) = getGitHubApiConfig

    val repo = new Repository(Some(username), Some(repositoryName), None, None)
    Async {
      repo.getAllInfo(githubApiUrl, githubGetRepoCommitsPath).map { repo =>
        repo match {
          case None       => NotFound(views.html.showRepoInfoNotFound())
          case Some(repo) => Ok(views.html.showRepoInfoOk(repo))
        }
      }
    }
  }
}

