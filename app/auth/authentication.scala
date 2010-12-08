package auth

import play.mvc.Http.Response
import play.mvc.Http.Request
import play._
import play.mvc._
import play.libs._
import models._
import java.util.{Map=>JMap}

object Authentication {
    def check(): Boolean = {
        try {
            val username = Request.current().user
            val password = Request.current().password
            val user = User.findBy("username", username).get(0).asInstanceOf[User]
            if(!user.password.equals(password)) return false
            Request.current().args.put("user", user)
        } catch {
            case e => return false
        }
        
        return true
    }
    
    def getAuthorizedUser() = Request.current().args.get("user").asInstanceOf[User]
}
