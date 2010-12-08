package controllers

import auth.Authentication
import play.mvc.results.Result
import play._
import play.mvc._
import play.libs._

trait Secure extends Controller {
    @Before def authenticate: Result = {
        if(!Authentication.check) {
            return Unauthorized
        }
        
        return null
    }
}
