package controllers

import auth.Authentication
import play.mvc.results.Result
import play._
import play.mvc._
import play.libs._

trait Secure extends Controller {
    val SEPARATOR = "|||"
    
    @Before def authenticate: Unit = {
        if(!Authentication.check()) return Forbidden
    }
}
