package controllers

import play.data.validation.Required
import play.data.validation.Validation
import play.data.validation.Valid
import validation.Captcha
import captcha.CaptchaManager
import net.sf.oval.ConstraintViolation
import net.sf.oval.Validator
import validation.Unique
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
    
    def getCaptcha() = MFJson("{\"id\":"+CaptchaManager.generate+"}")
    
    def addUser(@Valid user: User, @Required @Captcha captcha: String): Result = {
        if(Validation.hasErrors) return MFJsonError(validation.errorsMap)
        return MFJson(user.save)
    }
    
    def getUser(userId: Long) = MFJson(User.findById(userId).getOrNotFound)
    
    def search(query: String) = MFJson(User.search(query))
    
}

object APISecure extends APIController with Secure {
    
    def addFriend(userId: Long, friendId: Long) = {
        val user = User.findById(userId).getOrNotFound
        val friend = User.findById(friendId).getOrNotFound
        user.friends.add(friend)
        user.save()
        MFJson(user)
    }
    
    def setMood(userId: Long, mood: Boolean) = {
        val user = User.findById(userId).getOrNotFound
        user.setState(mood)
        user.save()
        MFJson(user)
    }
}
