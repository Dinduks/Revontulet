package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws._
import play.api.libs.json._

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
}

