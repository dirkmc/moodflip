package api

import scala.collection.mutable.HashMap
import java.util.Date
import java.lang.reflect.{Type, ParameterizedType}
import com.google.gson._
import org.scalatest.matchers.ShouldMatchers
import com.google.gson.reflect.TypeToken
import java.io.File
import play.mvc.Http._ 
import play.test._
import org.junit._
import models._
import java.util.{Map=>JMap, HashMap=>JHashMap}

abstract class ApiTestCase extends FunctionalTestCase with Matchers with ShouldMatchers {

    def checkUser(user: UserResponse, username: String, name: String, time: Long, state: String,
            password: String, friends: Array[Long] = null) {
        user.username should be (username)
        user.name should be (name)
        user.created.getTime should be (time plusOrMinus 2000)
        user.state should be (state)
        user.password should be (password)
        if(friends != null) {
            user.friends should have size (friends.size)
            user.friends.foreach(friend => friends should contain (friend.id.toLong))
        }
    }
    
    def parseUsers(response: Response): Array[UserResponse] = {
        val classType = classOf[Array[UserResponse]]
        val content = response.out.toString("utf-8")
        return gson().fromJson(content, classType)

    }
    
    def parseUser(response: Response): UserResponse = {
        val classType = classOf[UserResponse]
        val content = response.out.toString("utf-8")
        return gson().fromJson(content, classType)
    }
    
    def parseUserExists(response: Response): UserExistsResponse = {
        val classType = classOf[UserExistsResponse]
        val content = response.out.toString("utf-8")
        return gson().fromJson(content, classType)
    }
    
    
    def parseErrors(response: Response): Map[String, Array[ErrorResponse]] = {
        val classType = new TypeToken[JHashMap[String, Array[ErrorResponse]]]() {}.getType()
        val content = response.out.toString("utf-8")
        val res: JHashMap[String, Array[ErrorResponse]] = gson().fromJson(content, classType)
        val map = new HashMap[String, Array[ErrorResponse]]()
        res.keySet.toArray.foreach{ key => map += key.toString -> res.get(key) }
        Map[String, Array[ErrorResponse]]() ++ map
    }
    
    def parseError(response: Response): ErrorResponse = {
        val classType = classOf[ErrorResponse]
        val content = response.out.toString("utf-8")
        return gson().fromJson(content, classType)
    }
    
    def gson() = {
        val builder = new GsonBuilder()
        builder.registerTypeAdapter(classOf[UserResponse], new UserResponseIC)
        builder.registerTypeAdapter(classOf[UserExistsResponse], new UserExistsResponseIC)
        builder.registerTypeAdapter(classOf[ErrorResponse], new ErrorResponseIC)
        builder.registerTypeAdapter(classOf[List[_]], new ListIC)
        builder.create
    }
    
    case class UserResponse(id: String, username: String, password: String, name: String,
            state: String, created: Date, friends: Array[UserResponse])
    class UserResponseIC extends InstanceCreator[UserResponse] {
        def createInstance(classType: Type): UserResponse = new UserResponse(null, null, null, null, null, null, null)
    }
    
    case class UserExistsResponse(exists: String)
    class UserExistsResponseIC extends InstanceCreator[UserExistsResponse] {
        def createInstance(classType: Type): UserExistsResponse = new UserExistsResponse(null)
    }
    
    case class ErrorResponse(field: String, code: String, message: String, variables: Array[String])
    class ErrorResponseIC extends InstanceCreator[ErrorResponse] {
        def createInstance(classType: Type): ErrorResponse = new ErrorResponse(null, null, null, null)
    }
    
    class ListIC extends InstanceCreator[List[_]] {
        def createInstance(classType: Type): List[_] = Nil
    }
}
