package auth

import play.mvc.Http.Response
import play.mvc.Http.Request
import play._
import play.mvc._
import play.libs._
import models._
import java.util.{Map=>JMap}

object Authentication {
    val SEPARATOR = "XXX"
    val AUTH_TOKEN = "auth-token"
    val EXPIRY = 2 * 60 * 60 * 1000
    
    def check(): Boolean = {
        val userId = Request.current().params.get("userId")
        if(userId == null) return true
        
        try {
            val authTokenCookie = Request.current().cookies.get(AUTH_TOKEN)
            if(authTokenCookie == null) return false
            
            val authToken = authTokenCookie.value
            val parts = authToken.split(SEPARATOR)
            val Array(id, timestampStr, token) = authToken.split(SEPARATOR)
            if(!id.equals(userId)) return false
            
            val timestamp = timestampStr.toLong
            if(timestamp + EXPIRY < System.currentTimeMillis) return false
            
            val user = User.findById(id.toLong).head
            if(user == null) return false
            
            if(!getAuthToken(user, timestamp).equals(token)) return false
            
            Request.current().args.put("user", user)
            setAuthCookie(user)
            return true
            
        } catch {
            case _ => return false
        }
    }

    def authenticate(user: User, password: String): Boolean = {
        if(password == null || !password.equals(user.password)) return false
        setAuthCookie(user)
        return true
    }
    
    def setAuthCookie(user: User) = {
        val timestamp = System.currentTimeMillis
        val value = user.id + SEPARATOR + timestamp + SEPARATOR + getAuthToken(user, timestamp)
        Response.current().setCookie(AUTH_TOKEN, value, "3650d")        
    }
    
    def getAuthToken(user: User, timestamp: Long) = Crypto.sign(timestamp + "user" + user.id)

}
