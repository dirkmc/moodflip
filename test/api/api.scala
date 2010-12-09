package api

import java.util.Date
import java.lang.reflect.Type
import com.google.gson.InstanceCreator
import org.scalatest.matchers.ShouldMatchers
import com.google.gson.reflect.TypeToken
import com.google.gson.GsonBuilder
import java.io.File
import play.mvc.Http._ 
import play.test._
import org.junit._
import models._
import java.util.{Map=>JMap, HashMap=>JHashMap}

abstract class ApiTestCase extends FunctionalTestCase with Matchers with ShouldMatchers {
    
    def checkUser(user: UserResponse, username: String, name: String, time: Long, state: String,
            password: String, friends: List[Long]) {
        user.username should be (username)
        user.name should be (name)
        user.created.getTime should be (time plusOrMinus 2000)
        user.state should be (state)
        user.password should be (password)
        user.friends should have size (friends.size)
        user.friends.foreach(friend => friends should contain (friend))
    }
    
    def parseUsers(response: Response): Array[UserResponse] = {
        val classType = classOf[Array[UserResponse]]
        val content = response.out.toString("utf-8");
        val users: Array[UserResponse] = gson().fromJson(content, classType)
        return users
    }
    
    def parseUser(response: Response): UserResponse = {
        val classType = classOf[UserResponse]
        val content = response.out.toString("utf-8");
        val user: UserResponse = gson().fromJson(content, classType)
        return user
    }
    
    def gson() = {
        val builder = new GsonBuilder()
        builder.registerTypeAdapter(classOf[UserResponse], new UserResponseIC)
        builder.registerTypeAdapter(classOf[List[_]], new ListIC)
        builder.create
    }
    
    case class UserResponse(username: String, password: String, name: String,
            state: String, created: Date, friends: Array[Long])
    class UserResponseIC extends InstanceCreator[UserResponse] {
        def createInstance(classType: Type): UserResponse = new UserResponse(null, null, null, null, null, null)
    }
    
    class ListIC extends InstanceCreator[List[_]] {
        def createInstance(classType: Type): List[_] = Nil
    }
}
