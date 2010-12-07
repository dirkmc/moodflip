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
    
    def login(username: String, password: String): Result = {
        val user = User.find("byUsername", username).first.getOrNotFound
        if(Authentication.authenticate(user, password)) return MFJson(user)
        return Forbidden
    }
    
    // TODO:
    def newUser(user: User) = {
        Json(user.save())
    }
    
    def getUser(userId: Long) = MFJson(User.findById(userId).getOrNotFound)
    
    def search(query: String) = MFJson(User.search(query))
    
}

object APISecure extends APIController with Secure {
    
    // TODO:
    def newUser(user: User) = {
        Json(user.save())
    }
    
    def getUser(userId: Long) = MFJson(User.findById(userId).getOrNotFound)
    
    def search(query: String) = MFJson(User.search(query))
    
    def addFriend(userId: Long, friendId: Long) = {
        val user = User.findById(userId).getOrNotFound
        val friend = User.findById(friendId).getOrNotFound
        user.friends.add(friend)
        user.save()
        MFJson(user)
    }
    
    def setMood(userId: Long, mood: Boolean) = {
        val user = User.findById(userId).getOrNotFound
        // TODO: null == false is always false
        if(mood == null) NotFound
        user.setState(mood)
        user.save()
        MFJson(user)
    }
}
