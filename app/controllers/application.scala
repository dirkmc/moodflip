package controllers

import play.test.Fixtures
import play._
import play.mvc._
import models._

object Application extends Controller {

    def init = {
        val user = new User("fatguy", "password", "Fat Guy").save()
        user.setState(false)
        
        val happyGuy = new User("happyguy", "password", "Happy Guy").save()
        happyGuy.setState(true)
        
        val sadGuy = new User("sadguy", "password", "Sad Guy").save()
        sadGuy.setState(false)
        
        user.friends.add(happyGuy)
        user.friends.add(sadGuy)
        user.save()
    }
    def clear = {
        Fixtures.delete()
    }

    def index = Template
    
    def signUp = Template
    def apiSignUp = Template
    
    def addUser(user: User) = {
        user.save()
        Action(friends)
    }
    
    def friends() = {
        clear
        init
        val user = getUser()
        Template(user)
    }
    
    def search(query: String) = {
        val user = getUser
        val friends = User.search(query)
        Template(user, friends)
    }
    
    def addFriend(userId: Long, friendId: Long) = {
        val user = User.findById(userId).getOrNotFound
        val friend = User.findById(friendId).getOrNotFound
        user.friends.add(friend)
        user.save()
        Action(friends)
    }
    
    def setMood(userId: Long, mood: Boolean) = {
        val u = User.findById(userId).getOrNotFound
        u.setState(mood)
        u.save()
        Action(user(u.id))
    }
    
    def user(userId: Long) = Template("user" -> User.findById(userId).getOrNotFound)
    
    def getUser() = {
        val users = User.findAll()
        users(0)
    }
}
