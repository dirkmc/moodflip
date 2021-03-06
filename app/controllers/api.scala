package controllers

import java.util.Date
import captcha.CaptchaManager.CaptchaAuth
import play.data.validation.Required
import play.data.validation.Validation
import play.data.validation.Valid
import validation.Captcha
import captcha.CaptchaManager
import net.sf.oval.ConstraintViolation
import net.sf.oval.Validator
import java.util.{Map=>JMap, List=>JList}
import play.data.validation.{Error=>PlayError}
import play.mvc.results.Result
import play._
import play.mvc._
import models._
import play.mvc.results.Result
import auth.Authentication
import json._
import java.lang.reflect.Type
import com.google.gson.reflect.TypeToken
import com.google.gson._


class APIController extends Controller {
    def MFJson(obj: Any) = Json(MFSerializer.toJson(obj))
    
    def MFJson(obj: Any, adaptors: Map[java.lang.reflect.Type, Object]) = {
        Json(MFSerializer.toJson(obj, adaptors))
    }
    
    def MFJsonError(errors: JMap[String, JList[PlayError]]) = {
        response.status = 400
        
        for(key <- errors.keySet.toArray) {
            val errorList = errors.get(key)
            if(errorList.size > 0) {
                val message = errorList.get(0).message()
                if(message.equals("validation.object") || message.equals("Validation failed")) {
                    errors.remove(key)
                }
            }
        }
        
        Json(MFSerializer.toJson(errors))
    }
}

object API extends APIController {
    
    def getCaptcha(captchaId: String) = CaptchaManager.generate(captchaId)
    
    def addUser(@Valid user: User, @Required @Captcha captcha: CaptchaAuth): Result = {
        if(Validation.hasErrors) return MFJsonError(validation.errorsMap)
        return MFJson(user.save)
    }
    
    def getUser(userId: Long) = MFJson(User.findById(userId).getOrNotFound)
    
    def getUpdates(userId: Long, since: Date) = {
        val updates = User.findById(userId).getOrNotFound.updates(since)
        MFJson(updates, Map(classOf[State] -> new UserUpdateSerializer))
    }
    
    def search(query: String) = MFJson(User.search(query))
    
    def usernameExists(username: String) = {
        case class Response(exists: Boolean)
        MFJson(new Response(User.count("username = ?", username) > 0))
    }
}

object APISecure extends APIController with Secure {
    
    def addFriend(friendId: Long) = {
        val user = Authentication.getAuthorizedUser
        val friend = User.findById(friendId).getOrNotFound
        user.friends.add(friend)
        user.save()
        MFJson(user)
    }
    
    def setMood(mood: Boolean) = {
        val user = Authentication.getAuthorizedUser
        user.setState(mood)
        user.save()
        MFJson(user)
    }
}
