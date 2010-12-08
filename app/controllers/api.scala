package controllers

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
}

object API extends APIController {
    
    def addUser(user: User) = {
        MFJson(user.save())
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
